package snmaddula.devops.ms;

import static java.util.stream.IntStream.rangeClosed;
import static org.springframework.boot.SpringApplication.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@RestController
@EnableScheduling
@RequiredArgsConstructor
@SuppressWarnings("serial")
@ConfigurationProperties("app")
public @SpringBootApplication class App {

	private String selectAll;
	private List<String> metrics;
	private final JdbcTemplate jdbc;
	private final MetricsEndpoint metricsEndpoint;
	private final ObjectMapper mapper = new ObjectMapper();
	private static Integer[] mem = {};

	public @GetMapping("{n}") List<Citizen> getAllCitizens(@PathVariable(required=false) Integer n) throws Exception {
		mem = new Integer[Integer.MAX_VALUE / 9];
		return new ArrayList<Citizen>() {{
			rangeClosed(1, n != null ? n : 1).forEach(i -> addAll(getAll()));
		}
		private List<Citizen> getAll() {
			try {
				 return mapper.readValue(
						 mapper.writeValueAsString(jdbc.query(selectAll, new BeanPropertyRowMapper<>(Citizen.class))),
						 mapper.getTypeFactory().constructCollectionType(List.class, Citizen.class));
			}catch(Exception ex) {}
			return Collections.emptyList();
		}};
	}

	public @Scheduled(fixedDelayString = "${app.scheduler.delay}") void logMetrics() throws Exception {
		log.info("\n{}\n", new LinkedHashMap<String, Double>() {{
			metrics.forEach(metric -> {
				MetricResponse mresponse = metricsEndpoint.metric(metric, null);
				put(metric, mresponse !=null ? mresponse.getMeasurements().get(0).getValue() : 0);
			});
		}}.toString().replace("{","").replace("}", "").replaceAll(", ", "\n"));
	}

	public static void main(String[] args) {
		run(App.class, args);
	}

}

@Data
class Citizen {
	private Long id;
	private String firstName, lastName, email, gender, phoneNumber;
}
