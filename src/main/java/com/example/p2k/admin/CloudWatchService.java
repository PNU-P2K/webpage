package com.example.p2k.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
@Service
public class CloudWatchService {

    String identifier = "InstanceId";
    String instanceId = "i-0f1336b61e3b8d2a5";
    ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    Region region = Region.AP_NORTHEAST_2;
    Instant start = LocalDate.now(ZONE_ID).minusDays(1).atStartOfDay(ZONE_ID).toInstant();
    Instant end = LocalDate.now(ZONE_ID).atStartOfDay(ZONE_ID).toInstant();
    String namespace = "AWS/EC2";

    public MetricDataResponse getCPUUtilization(){
        return getMetricDataResponse("CPUUtilization", "cpuUtilizationQuery");
    }

    public MetricDataResponse getStatusCheckFailedSystem(){
        return getMetricDataResponse("StatusCheckFailed_System", "statusCheckFailedSystemQuery");
    }

    public MetricDataResponse getStatusCheckFailedInstance(){
        return getMetricDataResponse("StatusCheckFailed_Instance", "statusCheckFailedInstanceQuery");
    }

    public MetricDataResponse getNetworkIn(){
        return getMetricDataResponse("NetworkIn", "networkInQuery");
    }

    public MetricDataResponse getNetworkOut(){
        return getMetricDataResponse("NetworkOut", "networkOutQuery");
    }

    public MetricDataResponse getNetworkPacketsIn(){
        return getMetricDataResponse("NetworkPacketsIn", "networkPacketInQuery");
    }

    public MetricDataResponse getNetworkPacketsOut(){
        return getMetricDataResponse("NetworkPacketsOut", "networkPacketOutQuery");
    }

    public MetricDataResponse getDiskReads(){
        return getMetricDataResponse("DiskReadBytes", "diskReadQuery");
    }

    public MetricDataResponse getDiskReadOperations(){
        return getMetricDataResponse("DiskReadOps", "diskReadOpsQuery");
    }

    public MetricDataResponse getDiskWrites(){
        return getMetricDataResponse("DiskWriteBytes", "diskWriteQuery");
    }

    public MetricDataResponse getDiskWriteOperations(){
        return getMetricDataResponse("DiskWriteOps", "diskWriteOpsQuery");
    }

    private MetricDataResponse getMetricDataResponse(String metricName, String dataQueryId) {
        int period = 300; //메트릭 데이터 주기(5분)
        int maxDataPoints = 100; //가져올 데이터 포인트 수
        String stat = "Average";

        AwsCredentialsProvider credentialsProvider = getCredentialsProvider();

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
                    .id(dataQueryId)
                    .returnData(true)
                    .build();

            List<MetricDataQuery> queries = new ArrayList<>();
            queries.add(dataQuery);

            GetMetricDataRequest request = GetMetricDataRequest.builder()
                    .startTime(start)
                    .endTime(end)
                    .metricDataQueries(queries)
                    .maxDatapoints(maxDataPoints)
                    .build();

            GetMetricDataResponse response = cloudWatchClient.getMetricData(request);
            List<Instant> timestamps = new ArrayList<>();
            List<Double> values = new ArrayList<>();

            for (MetricDataResult result : response.metricDataResults()) {
                timestamps = result.timestamps();
                values = result.values();

                log.info("id=" + result.id());
                for (int i=values.size()-1; i>=0; i--) {
                    log.info("timestamp=" + timestamps.get(i).atZone(ZONE_ID) + ", value=" + values.get(i));
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

    private static AwsCredentialsProvider getCredentialsProvider() {
        String accessKey = "AKIAXTAISJOS7G2P6CTN";
        String secretKey = "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";
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return StaticCredentialsProvider.create(credentials);
    }
}
