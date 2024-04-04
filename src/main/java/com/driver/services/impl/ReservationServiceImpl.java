package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user;
        try {
            user = userRepository3.findById(userId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        ParkingLot parkingLot;
        try {
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        ArrayList<Spot> spotArrayList = new ArrayList<>();
        for(Spot s : parkingLot.getSpotList()){
            if(!s.getOccupied()){
                int wheel = 0;
                if(s.getSpotType() == SpotType.TWO_WHEELER){
                    wheel = 2;
                } else if (s.getSpotType() == SpotType.FOUR_WHEELER) {
                    wheel = 4;
                }else {
                    wheel = Integer.MAX_VALUE;
                }
                if(wheel >= numberOfWheels){
                    spotArrayList.add(s);
                }
            }
        }

        if(spotArrayList.isEmpty()){
            throw new Exception("Cannot make reservation");
        }

//        spotArrayList.sort((a,b) -> (a.getPricePerHour() - b.getPricePerHour()));
        spotArrayList.sort(Comparator.comparingInt(Spot::getPricePerHour));

        Spot spot = spotArrayList.get(0);
        spot.setOccupied(true);

        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(spot);
        reservation.setUser(user);

        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(spot);

        return reservation;
    }
}
