package com.jit.sports.service;

import com.jit.sports.entry.SportInfo;
import com.jit.sports.entry.UserInfo;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    UserInfo login(String userName, String password);
    String existUserName(String userName);
    void reg(String userName, String password);
    void insertSport(String tag, String userName, String startTime, String overTime,
                     double totalDistance, double totalUp, double totalDown,double averageSpeed,
                     double maxSpeed, double maxElevation, double minElevation);
    SportInfo[] selectSportByName(String userName);
}
