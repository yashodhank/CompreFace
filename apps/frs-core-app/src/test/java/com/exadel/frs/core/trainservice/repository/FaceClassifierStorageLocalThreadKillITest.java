package com.exadel.frs.core.trainservice.repository;

import com.exadel.frs.core.trainservice.component.classifiers.LogisticRegressionExtendedClassifier;
import com.exadel.frs.core.trainservice.dao.FaceDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.EnabledIf;

@SpringBootTest
@Slf4j
@EnabledIf(expression = "#{environment.acceptsProfiles('integration-test')}")
public class FaceClassifierStorageLocalThreadKillITest {

    private FaceClassifierStorageLocal storage;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FaceDao faceDao;

    private static final String APP_KEY = "app";
    private static final String MODEL_ID = "model_id";

    @BeforeEach
    void setUp() {
        storage = new FaceClassifierStorageLocal(applicationContext);
        storage.postConstruct();
    }

    @Test
    public void unlock() throws InterruptedException {
        var faceClassifier = storage.getFaceClassifier(APP_KEY, MODEL_ID);
        faceClassifier.setStorage(storage);
        faceClassifier.setClassifier(new LogisticRegressionExtendedClassifier());
        storage.lock(APP_KEY, MODEL_ID);
        faceClassifier.train(faceDao.findAllFaceEmbeddings(), APP_KEY, MODEL_ID);
        storage.unlock(APP_KEY, MODEL_ID);
        //Thread.sleep(10000L);
    }
}