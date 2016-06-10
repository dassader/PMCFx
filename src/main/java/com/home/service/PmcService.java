package com.home.service;

import java.io.File;
import java.util.List;

public interface PmcService {
    List<String> getActivitiesList();

    void fillTime(String activityName, String time);

    File makeScreenShot();
}
