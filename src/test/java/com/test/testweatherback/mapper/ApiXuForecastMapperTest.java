package com.test.testweatherback.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.testweatherback.client.GenericForecastClient;
import com.test.testweatherback.client.response.apixu.ApiXuResponse;
import com.test.testweatherback.client.response.darksky.DarkSkyResponse;
import com.test.testweatherback.config.SpringTestConfig;
import com.test.testweatherback.dto.DayForecast;
import com.test.testweatherback.dto.Forecast;
import com.test.testweatherback.enumeration.TemperatureUnit;
import com.test.testweatherback.exception.InvalidForecastResponseException;
import com.test.testweatherback.test.util.AssertUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SpringTestConfig.class)
@ActiveProfiles("test")
public class ApiXuForecastMapperTest {

  @Autowired
  @Qualifier("apiXuMapper")
  GenericForecastMapper target;

  private ObjectMapper objMap = new ObjectMapper();

  @Test
  public void givenForecastSourceResponse_WhenMappingIsRequiredAndInputIsValid_ThenReturnMapped()
      throws IOException {

    InputStream resource = new ClassPathResource(
        "mock/apixu/apixu-ok.response.json").getInputStream();

    ApiXuResponse input = objMap.readValue(resource, ApiXuResponse.class);

    Forecast forecast =  (Forecast) target.mapSourceResponse(input, TemperatureUnit.CELSIUS);

    assert(Objects.nonNull(forecast));
    AssertUtils.assertDayForecastFields(forecast.getTodayForecast());

    assert(Objects.nonNull(forecast.getNextDaysForecast()));

    for(DayForecast day : forecast.getNextDaysForecast()) {
      AssertUtils.assertDayForecastFields(day);
    }

  }

  @Test(expected = InvalidForecastResponseException.class)
  public void givenForecastSourceResponse_WhenMappingIsRequiredAndInputHasMissingFields_ThenReturnException()
      throws IOException {

    InputStream resource = new ClassPathResource(
        "mock/apixu/apixu-missing-fields.response.json").getInputStream();

    ApiXuResponse input = objMap.readValue(resource, ApiXuResponse.class);

    target.mapSourceResponse(input, TemperatureUnit.CELSIUS);

  }

  @Test(expected = InvalidForecastResponseException.class)
  public void givenForecastSourceResponse_WhenMappingIsRequiredAndInputIsPartialEmpty_ThenReturnException()
      throws IOException {

    InputStream resource = new ClassPathResource(
        "mock/apixu/apixu-partial-empty.response.json").getInputStream();

    ApiXuResponse input = objMap.readValue(resource, ApiXuResponse.class);

    target.mapSourceResponse(input, TemperatureUnit.CELSIUS);
  }

  @Test(expected = InvalidForecastResponseException.class)
  public void givenForecastSourceResponse_WhenMappingIsRequiredAndInputIsTotallyEmpty_ThenReturnException()
      throws IOException {

    ApiXuResponse input = objMap.readValue("{}", ApiXuResponse.class);

    target.mapSourceResponse(input, TemperatureUnit.CELSIUS);


  }



}
