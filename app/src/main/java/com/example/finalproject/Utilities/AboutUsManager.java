package com.example.finalproject.Utilities;

public class AboutUsManager {


    public static String getDescription() {
        String description= "We are a professional barbershop that offers a wide range of services";
        return description;
    }

    public static String getEmail() {
        String email= "";
        return email;
    }

    public static String[][] getInfoArray() {
        String info[][] = new String[2][2];
        info[0][0] = "Sunday - Thursday";
        info[0][1] = "9:00 - 17:00";
        info[1][0] = "Friday - Saturday";
        info[1][1] = "Closed";
        return info;
    }

    public static String getAdress() {
        String adress= "Our adress is: Trumpeldor St 20, Petah Tikva";
        return adress;
    }
}
