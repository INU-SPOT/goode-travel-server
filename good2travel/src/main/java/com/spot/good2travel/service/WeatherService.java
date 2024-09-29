package com.spot.good2travel.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.WeatherReadException;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.LocalGovernment;
import com.spot.good2travel.domain.Weather;
import com.spot.good2travel.dto.WeatherResponse;
import com.spot.good2travel.repository.LocalGovernmentRepository;
import com.spot.good2travel.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final LocalGovernmentRepository localGovernmentRepository;
    private final WeatherRepository weatherRepository;
    private final WebClient webClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.weather-api-key}")
    private String weatherApiKey;

    //매 시 35분마다 시행
    @Scheduled(cron = "0 35 * * * *")
    public void getWeatherAPI(){
        setLocalGovernmentWeather();
    }

    //매일 00:10분에 실행
    @Scheduled(cron = "00 10 00 * * *")
    public void getDay() throws URISyntaxException {
        getDayData();
        updateDate();
    }

    public void setLocalGovernmentWeather(){
        List<LocalGovernment> localGovernments = localGovernmentRepository.findAll();

        localGovernments.forEach(localGovernment -> {
            Weather weather = weatherRepository.findByLocalGovernment(localGovernment);
            try {
                log.info("철희중인 지역: {}", localGovernment.getId().toString());
                String temperature = getTemperature(localGovernment);
                String sky = getWeatherSky(localGovernment);
                weather.updateWeather(temperature, sky);
                weatherRepository.save(weather);
                Thread.sleep(2000);
            } catch (URISyntaxException | InterruptedException e) {
                throw new WeatherReadException(ExceptionMessage.WEATHER_READ_EXCEPTION);
            }
        });
    }

    public String getWeatherSky(LocalGovernment localGovernment) throws URISyntaxException {
        LocalDateTime t = LocalDateTime.now().minusMinutes(30);
        String x = localGovernment.getCoordinateX();
        String y = localGovernment.getCoordinateY();
        URI url = new URI("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"+
                "?ServiceKey="+ weatherApiKey+
                "&pageNo=1"+
                "&numOfRows=20"+
                "&dataType=JSON"+
                "&base_date=" + t.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "&base_time=" + t.format(DateTimeFormatter.ofPattern("HHmm"))
                + "&nx=" + x
                + "&ny=" + y);

        JsonArray itemList = getJsonData(url);
        String sky = "";
        String pty = "";
        int index = 0;
        for (int i = 0; i < itemList.size(); i++) {
            JsonObject item = itemList.get(i).getAsJsonObject();
            String category = item.get("category").getAsString();
            if (category.equals("PTY")) {
                pty = item.get("fcstValue").getAsString();
                index = i;
                break;
            }
        }
        for (int i = index; i < itemList.size(); i++) {
            JsonObject item = itemList.get(i).getAsJsonObject();
            String category = item.get("category").getAsString();
            if (category.equals("SKY")) {
                sky = item.get("fcstValue").getAsString();
                break;
            }
        }
        String weather= "";
        if(pty.equals("0")){
            if(sky.equals("1")){
                weather="맑음";
            }
            else if(sky.equals("3")||sky.equals("4")){
                weather="구름";
            }
        }
        else{
            weather = switch (pty) {
                case "1", "5" -> "비";
                case "2", "6" -> "진눈깨비";
                case "3", "7" -> "눈";
                default -> weather;
            };
        }
        return weather;
    }


    public String getTemperature(LocalGovernment localGovernment) throws URISyntaxException {
        LocalDateTime t = LocalDateTime.now().minusMinutes(30);

        String x = localGovernment.getCoordinateX();
        String y = localGovernment.getCoordinateY();

        URI url = new URI("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"+
                "?ServiceKey="+weatherApiKey+
                "&pageNo=1"+
                "&numOfRows=10"+
                "&dataType=JSON"+
                "&base_date=" + t.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "&base_time=" + t.format(DateTimeFormatter.ofPattern("HHmm"))
                + "&nx=" + x
                + "&ny=" + y);
        log.info(url.toString());
        JsonArray itemList = getJsonData(url);

        String temperature = "20";

        String pty = "";
        int index = 0;
        for (int i = 0; i < itemList.size(); i++) {
            JsonObject item = itemList.get(i).getAsJsonObject();
            String category = item.get("category").getAsString();
            if (category.equals("PTY")) {
                pty = item.get("obsrValue").getAsString();
                index = i;
                break;
            }
        }
        for (int i = index; i < itemList.size(); i++) {
            JsonObject item = itemList.get(i).getAsJsonObject();
            String category = item.get("category").getAsString();
            if (category.equals("T1H")) {
                temperature = item.get("obsrValue").getAsString();
                break;
            }
        }
        return temperature;
    }

    public JsonArray getJsonData(URI url){
        String result = webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.just(new WeatherReadException(ExceptionMessage.WEATHER_READ_EXCEPTION)))
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .block();
        try {
            System.out.println(result);
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            JsonObject body = jsonObject.getAsJsonObject("response").getAsJsonObject("body");
            JsonArray itemList = body.getAsJsonObject("items").getAsJsonArray("item");
            return itemList;
        }
        catch (Exception e){
            log.info("날씨 관련 api호출 중 문제 발생 ");
            return new JsonArray();
        }
    }

    public void updateDate(){
        List<LocalGovernment> localGovernments = localGovernmentRepository.findAll();
        localGovernments.forEach(localGovernment -> {
            Weather weather = weatherRepository.findByLocalGovernment(localGovernment);
            weather.updateDate(LocalDate.now());
        });
    }

    public void getDayData() throws URISyntaxException {
        LocalDate t = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");

        URI url = new URI("http://apis.data.go.kr/B090041/openapi/service/RiseSetInfoService/getAreaRiseSetInfo" +
                "?location=%EC%9D%B8%EC%B2%9C" +
                "&locdate=" + t.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "&serviceKey=" + weatherApiKey);

        String result = webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.just(new WeatherReadException(ExceptionMessage.WEATHER_READ_EXCEPTION)))
                .bodyToMono(String.class)
                .block();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(result)));
            document.getDocumentElement().normalize();

            NodeList itemList = document.getElementsByTagName("item");
            if (itemList.getLength() == 0) {
                log.error("item 태그를 찾을 수 없습니다. 응답 데이터 확인 필요.");
                return;
            }

            Element item = (Element) itemList.item(0);

            NodeList sunriseList = item.getElementsByTagName("sunrise");
            if (sunriseList.getLength() == 0) {
                log.error("sunrise 태그를 찾을 수 없습니다.");
                return;
            }
            String sunriseText = sunriseList.item(0).getTextContent().trim();
            LocalTime sunrise = LocalTime.parse(sunriseText, formatter);

            NodeList sunsetList = item.getElementsByTagName("sunset");
            if (sunsetList.getLength() == 0) {
                log.error("sunset 태그를 찾을 수 없습니다.");
                return;
            }
            String sunsetText = sunsetList.item(0).getTextContent().trim();
            LocalTime sunset = LocalTime.parse(sunsetText, formatter);

            redisTemplate.opsForValue().set("sunrise", sunrise.toString());
            redisTemplate.opsForValue().set("sunset", sunset.toString());

        } catch (Exception e) {
            log.error("일출일몰 api 파싱 중 문제 발생. 원인: {}", e.getMessage());
        }
    }

    public WeatherResponse getWeather(Item item){
        LocalGovernment localGovernment = item.getLocalGovernment();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Weather weather = localGovernment.getWeather();
        LocalDateTime now = LocalDateTime.now();

        String sky = weather.getSky();
        String temperature = weather.getTemperature();
        LocalTime sunset = LocalTime.parse(Objects.requireNonNull(redisTemplate.opsForValue().get("sunset")).toString(), formatter);
        LocalTime sunrise = LocalTime.parse(Objects.requireNonNull(redisTemplate.opsForValue().get("sunrise")).toString(), formatter);
        String day = now.toLocalTime().isAfter(sunset) && now.toLocalTime().isBefore(sunrise) ? "day":"night";
        String todayWeatherLink = localGovernment.getTodayWeatherUrl();
        return WeatherResponse.of(localGovernment.getMetropolitanGovernment().getName() + " "+ localGovernment.getName(), now, sky,
                temperature, day, todayWeatherLink);
    }

}