package com.infogain.gcp.poc.consumer.component;

import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
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

    public PubSubSubscriber(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
        this.grpcSubscriberStub = getSubscriberStub();
    }

    private GrpcSubscriberStub getSubscriberStub() {

        SubscriberStubSettings.Builder subscriberStubSettings = SubscriberStubSettings.newBuilder();

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
