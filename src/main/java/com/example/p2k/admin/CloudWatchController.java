package com.example.p2k.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cloudwatch")
public class CloudWatchController {

    String instancePublicIp = "3.37.55.57";
    String accessKey = "AKIAXTAISJOS7G2P6CTN";
    String secretKey = "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";
    AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

    @GetMapping("/cpuUsage")
    public List<MetricDataResult> getCPUUsageData(){

        Region region = Region.AP_NORTHEAST_2;
        CloudWatchClient cloudWatchClient = CloudWatchClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        Dimension dimension = Dimension.builder()
                .name("i-0f1336b61e3b8d2a5")
                .value(instancePublicIp)
                .build();

        try{
            Instant start = Instant.now().minusSeconds(3600); //시작 시간 설정(최근 1시간)
            Instant end = Instant.now();
            String namespace = "AWS/EC2";
            String metricName = "CPUUtilization";
            int period = 300; //메트릭 데이터 주기(5분)
            int maxDataPoints = 100; //가져올 데이터 포인트 수

            Metric metric = Metric.builder()
                    .namespace(namespace)
                    .metricName(metricName)
                    .dimensions(dimension)
                    .build();

            MetricStat metricStat = MetricStat.builder()
                    .stat("Average") //메트릭 통계 유형(평균)
                    .period(period)
                    .metric(metric)
                    .build();

            MetricDataQuery dataQuery = MetricDataQuery.builder()
                    .metricStat(metricStat)
                    .id("cpuUsageQuery")
                    .returnData(true)
                    .build();

            List<MetricDataQuery> queries = new ArrayList<>();
            queries.add(dataQuery);

            GetMetricDataRequest request = GetMetricDataRequest.builder()
                    .startTime(start)
                    .endTime(end)
                    .metricDataQueries(queries)
                    .scanBy("TimestampDescending")
                    .maxDatapoints(maxDataPoints)
                    .build();

            GetMetricDataResponse response = cloudWatchClient.getMetricData(request);

            for (MetricDataResult item : response.metricDataResults()) {
                System.out.println("The label is " + item.label());
                System.out.println("The status code is " + item.statusCode().toString());
            }
            return response.metricDataResults();

        }catch(CloudWatchException e){
            log.info(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return null;
        }finally{
            cloudWatchClient.close();
        }
    }
}
