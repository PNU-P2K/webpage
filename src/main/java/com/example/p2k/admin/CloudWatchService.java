package com.example.p2k.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CloudWatchService {

    private static final String IDENTIFIER = "BucketName";
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Region REGION = Region.AP_NORTHEAST_2;
    private static final String NAMESPACE = "AWS/S3";
    private static final int PERIOD = 300;
    private static final int MAX_DATA_POINTS = 100;

    private final Instant start = LocalDate.now(ZONE_ID).minusDays(1).atStartOfDay(ZONE_ID).toInstant();
    private final Instant end = LocalDate.now(ZONE_ID).atStartOfDay(ZONE_ID).toInstant();

    @Value("${cloudwatch.s3.bucket-name}")
    private String bucketName;

    @Value("${cloudwatch.credentials.access-key}")
    private String accessKey;

    @Value("${cloudwatch.credentials.secret-key}")
    private String secretKey;

    public MetricDataResponse getS3BucketSize(){
        return getMetricDataResponse("BucketSizeBytes", "s3BucketSizeQuery");
    }

    public MetricDataResponse getS3NumberOfObjects(){
        return getMetricDataResponse("NumberOfObjects", "s3NumberOfObjectsQuery");
    }

    private MetricDataResponse getMetricDataResponse(String metricName, String dataQueryId) {
        AwsCredentialsProvider credentialsProvider = getCredentialsProvider();

        try (CloudWatchClient cloudWatchClient = createCloudWatchClient(credentialsProvider)) {
            Dimension dimension = Dimension.builder()
                    .name(IDENTIFIER)
                    .value(bucketName)
                    .build();

            Metric metric = Metric.builder()
                    .namespace(NAMESPACE)
                    .metricName(metricName)
                    .dimensions(dimension)
                    .build();

            MetricStat metricStat = MetricStat.builder()
                    .stat("Average")
                    .period(PERIOD)
                    .metric(metric)
                    .build();

            MetricDataQuery dataQuery = MetricDataQuery.builder()
                    .metricStat(metricStat)
                    .id(dataQueryId)
                    .returnData(true)
                    .build();

            GetMetricDataRequest request = GetMetricDataRequest.builder()
                    .startTime(start)
                    .endTime(end)
                    .metricDataQueries(Collections.singletonList(dataQuery))
                    .maxDatapoints(MAX_DATA_POINTS)
                    .build();

            GetMetricDataResponse response = cloudWatchClient.getMetricData(request);

            List<Instant> timestamps = new ArrayList<>();
            List<Double> values = new ArrayList<>();

            response.metricDataResults().forEach(result -> {
                timestamps.addAll(result.timestamps());
                values.addAll(result.values());

                log.info("id=" + result.id());
                for (int i = values.size() - 1; i >= 0; i--) {
                    log.info("timestamp=" + timestamps.get(i).atZone(ZONE_ID) + ", value=" + values.get(i));
                }
            });

            return new MetricDataResponse(timestamps, values);
        } catch (CloudWatchException e) {
            log.info(e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    private AwsCredentialsProvider getCredentialsProvider() {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return StaticCredentialsProvider.create(credentials);
    }

    private CloudWatchClient createCloudWatchClient(AwsCredentialsProvider credentialsProvider) {
        return CloudWatchClient.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
