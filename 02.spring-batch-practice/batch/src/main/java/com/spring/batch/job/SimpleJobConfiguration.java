package com.spring.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job simpleJob() {
    return jobBuilderFactory.get("simpleJob") // simpleJob이란 이름의 Batch Job 생성
            .start(simpleStep1(null))
            .next(simpleStep2(null))
            .build();
  }

  @Bean
  @JobScope
  public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
    return stepBuilderFactory.get("simpleStep1") // simpleStep1이란 이름의 Batch Step 생
            .tasklet((contribution, chunkContext) -> { // Step 안에서 수행될 기능들 명
              log.info(">>>>>>>>>> STEP01");
                throw new IllegalArgumentException("step1 실패");
            })
            .build();
  }

  @Bean
  @JobScope
  public Step simpleStep2(@Value("#{jobParameters[reqquestDate]}") String requestDate) {
    return stepBuilderFactory.get("simpleStep2")
            .tasklet((contribution, chunkContext) -> {
              log.info(">>>>>>>>>> STEP02");
              log.info(">>>>>>>>>> requestDate = {}", requestDate);
              return RepeatStatus.FINISHED;
            })
      .build();
  }
}
