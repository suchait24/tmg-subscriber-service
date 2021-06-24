package com.infogain.gcp.poc.consumer.component;


import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
@Component
public class PubSubSubscriber {

    private PullRequest pullRequest;
    private GrpcSubscriberStub grpcSubscriberStub;
    private boolean useRetrySettings;
    private boolean useModifyAckSettings;
    private boolean useExecutorProvider;

    public PubSubSubscriber(PullRequest pullRequest) throws IOException {
        this.pullRequest = pullRequest;
        this.grpcSubscriberStub = getSubscriberStub();
    }

    private GrpcSubscriberStub getSubscriberStub() throws IOException {

        SubscriberStubSettings.Builder subscriberStubSettings = SubscriberStubSettings.newBuilder();

        ExecutorProvider executorProvider =
                InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(8).build();

        subscriberStubSettings.setExecutorProvider(executorProvider);
        /*
        subscriberStubSettings
                .setTransportChannelProvider(SubscriberStubSettings
                        .defaultGrpcTransportProviderBuilder()
                        .setMaxInboundMessageSize(20 * 1024 * 1024)
                        .build());

         */

        try {
            this.grpcSubscriberStub = GrpcSubscriberStub.create(subscriberStubSettings.build());
            return this.grpcSubscriberStub;
        } catch (IOException ioException) {
            throw new RuntimeException("Error creating the SubscriberStub", ioException);
        }
    }

    public List<ReceivedMessage> getPullResponse() {

        PullResponse pullResponse = this.grpcSubscriberStub.pullCallable().call(this.pullRequest);
        return pullResponse.getReceivedMessagesList();
    }

    public void acknowledgeMessageList(List<String> ackIds) {
        AcknowledgeRequest acknowledgeRequest = AcknowledgeRequest.newBuilder()
                .addAllAckIds(ackIds)
                .setSubscription(this.pullRequest.getSubscription()).build();

        this.grpcSubscriberStub.acknowledgeCallable().futureCall(acknowledgeRequest);
    }
}
