package com.example.p2k.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cloudwatch")
public class CloudWatchService {



    @GetMapping("/cpuUsage")
    public MetricDataResponse getCPUUsageData(){
        String identifier = "InstanceId";
        String instanceId = "i-0f1336b61e3b8d2a5";
        String instancePublicIp = "3.37.55.57";
        String accessKey = "AKIAXTAISJOS7G2P6CTN";

        ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
        Region region = Region.AP_NORTHEAST_2;
        Instant start = LocalDate.now(ZONE_ID).minusDays(1).atStartOfDay(ZONE_ID).toInstant();
        Instant end = LocalDate.now(ZONE_ID).atStartOfDay(ZONE_ID).toInstant();
        String namespace = "AWS/EC2";
        String metricName = "CPUUtilization";
        int period = 60; //메트릭 데이터 주기(5분)
        int maxDataPoints = 100; //가져올 데이터 포인트 수
        String stat = "Average";

        String secretKey = "gYidLTrhHf6LJwcs6sAimoo3v2Eoiw13T+IL8GUj";
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        CloudWatchClient cloudWatchClient = CloudWatchClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        try{

            Dimension dimension = Dimension.builder()
                    .name(identifier)
                    .value(instanceId)
                    .build();

            Metric metric = Metric.builder()
                    .namespace(namespace)
                    .metricName(metricName)
                    .dimensions(dimension)
                    .build();

            MetricStat metricStat = MetricStat.builder()
                    .stat(stat) //메트릭 통계 유형(평균)
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
                    //.scanBy("TimestampDescending")
                    .maxDatapoints(maxDataPoints)
                    .build();

            GetMetricDataResponse response = cloudWatchClient.getMetricData(request);
            List<Instant> timestamps = new ArrayList<>();
            List<Double> values = new ArrayList<>();

            for (MetricDataResult result : response.metricDataResults()) {
                timestamps = result.timestamps();
                values = result.values();

                System.out.println(String.format("id : %s", result.id()));
                for (int i=values.size()-1; i>=0; i--) {
                    System.out.println(String.format("timestamp : %s, value : %s", timestamps.get(i).atZone(ZONE_ID), values.get(i)));
                }
            }

            return new MetricDataResponse(timestamps, values);

        }catch(CloudWatchException e){
            log.info(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return null;
        }finally{
            cloudWatchClient.close();
        }
    }
}
