package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
//        Reservation reservation = reservationRepository2.findById(reservationId).orElseThrow(()->new Exception("Reservation not found !!"));
        Reservation reservation = null;
        try{
            reservation = reservationRepository2.findById(reservationId).get();
        }catch (Exception e){
            throw new Exception("Reservation not found !!");
        }

        Payment payment = new Payment();
        int bill = reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour();

        if(bill > amountSent){
            throw new Exception("Insufficient Amount !!");
        }

        if(mode.equalsIgnoreCase("cash")){
            payment.setPaymentMode(PaymentMode.CASH);
        }else if(mode.equalsIgnoreCase("upi")){
            payment.setPaymentMode(PaymentMode.UPI);
        }else if(mode.equalsIgnoreCase("card")){
            payment.setPaymentMode(PaymentMode.CARD);
        }else{
            throw new Exception("Payment mode not detected !!");
        }

        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);

        reservationRepository2.save(reservation);

        return payment;
    }
}
