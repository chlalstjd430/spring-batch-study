package com.spring.batch.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job stepNextConditionalJob() {
    return jobBuilderFactory.get("stepNextConditionalJob")
            .start(conditionalJobStep1())
              .on("FAILED") // FAILED 일 경우
              .to(conditionalJobStep3()) // step3으로 이동
              .on("*") // step3의 결과 관계 이
              .end() // step3으로 이동하면 Flow가 종료
            .from(conditionalJobStep1()) // step1로부터
              .on("*") // FAILED 외에 모든 경우
              .to(conditionalJobStep2()) // step2로 이동
              .next(conditionalJobStep3()) // step2가 정상 종료되면 step3로 이동한다.
              .on("*") // step3의 결과 관계 없이
              .end() // step3으로 이동하면 flow 종료
            .end() // Job 종료
            .build();
  }

  @Bean
  public Step conditionalJobStep1() {
    return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
              log.info(">>>>>>>>> conditionalJobStep1");

              /*
                ExitStatus를 FAILED로 지정한다.
                해당 status를 보고 flow가 진행된다.
               */

              // contribution.setExitStatus(ExitStatus.FAILED); // 상태값 FAILED 설정

              return RepeatStatus.FINISHED;
            })
            .build();
  }

  @Bean
  public Step conditionalJobStep2() {
    return stepBuilderFactory.get("step2")
      .tasklet((contribution, chunkContext) -> {
        log.info(">>>>>>>>> conditionalJobStep2");
        return RepeatStatus.FINISHED;
      })
      .build();
  }

  @Bean
  public Step conditionalJobStep3() {
    return stepBuilderFactory.get("step3")
      .tasklet((contribution, chunkContext) -> {
        log.info(">>>>>>>>> conditionalJobStep3");
        return RepeatStatus.FINISHED;
      })
      .build();
  }
}
