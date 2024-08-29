package com.amazon.ata.debugging.subscribeandsave.dao;


import com.amazon.ata.debugging.subscribeandsave.App;
import com.amazon.ata.debugging.subscribeandsave.test.util.SubscriptionRestorer;
import com.amazon.ata.debugging.subscribeandsave.types.Subscription;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class SubscriptionFileStorageTest {

    private static final String ASIN = "B01BMDAVIY";
    private static final String CUSTOMER_ID = "amzn1.account.AEZI3A063427738YROOFT8WCXKDE";

    private static final String SUBSCRIPTION_ID = "81a9792e-9b4c-4090-aac8-28e733ac2f54";
    private static final String invalidASIN2 = "amzn1.account.AEZI3A027560538W420H09ACTDP2";
    private static final String invalidCUSTOMER_ID2 = "B00006IEJB";

    private SubscriptionFileStorage classUnderTest;

    /**
     * The entry point, which results in calls to all test methods.
     *
     * @param args Command line arguments (ignored).
     */
    public static void main(String[] args) {
        SubscriptionFileStorageTest tester = new SubscriptionFileStorageTest();

        // clean up subscriptions before/after running runAllTests(), for when tests are invoked via main(),
        // rather than through a build
        tester.restoreSubscriptions();
        tester.runAllTests();
        tester.restoreSubscriptions();
    }

    @Test
    public void runAllTests() {
        classUnderTest = App.getSubscriptionFileStorage();
        boolean pass = true;

        pass = writeSubscription_newSubscription_SubscriptionReturnedWithId();
        pass = getSubscription_existingSubscription_SubscriptionReturnedWithMismatchedValues();

        if (!pass) {
            String errorMessage = "\n/!\\ /!\\ /!\\ The SubscriptionFileStorage tests failed. Test aborted. /!\\ /!\\ /!\\";
            System.out.println(errorMessage);
            fail(errorMessage);
        } else {
            System.out.println("The SubscriptionFileStorage tests passed!");
        }
    }

    public boolean writeSubscription_newSubscription_SubscriptionReturnedWithId() {
        // GIVEN - a new subscription to save
        Subscription newSubscription = Subscription.builder()
                                                   .withAsin(ASIN)
                                                   .withCustomerId(CUSTOMER_ID)
                                                   .withFrequency(1)
                                                   .build();

        // WHEN - create a new subscription
        Subscription result = classUnderTest.writeSubscription(newSubscription);

        // THEN a subscription should be returned and the id field should be populated
        if (result == null) {
            System.out.println("   FAIL: Writing subscription should return the subscription.");
            return false;
        }
        if (StringUtils.isBlank(result.getId())) {
            System.out.println("   FAIL: Writing subscription should return a subscription with an id field.");
            return false;
        }

        System.out.println("  PASS: Creating a new subscription succeeded.");
        return true;
    }


    public boolean getSubscription_existingSubscription_SubscriptionReturnedWithMismatchedValues() {
        // GIVEN - an existing subscription Id
        String subscriptionId = SUBSCRIPTION_ID;

        // WHEN - return an exisiting subscription using an existing subscriptionId
        Subscription result = classUnderTest.getSubscriptionById(subscriptionId);

        // THEN a subscription should be returned and the id field should be populated
        if (result.getAsin().equals(invalidASIN2)) {
            System.out.println("   FAIL: ASIN should not return CustomerId. Check getSubscriptionId");
            return false;
        }
        if (result.getCustomerId().equals(invalidCUSTOMER_ID2)) {
            System.out.println("   FAIL: CustomerId should not return ASIN. Check getSubscriptionId");
            return false;
        }

        System.out.println("  PASS: New subscription with no mismatched fields.");
        return true;
    }

    @BeforeEach
    @AfterEach
    private void restoreSubscriptions() {
        SubscriptionRestorer.restoreSubscriptions();
    }
}
