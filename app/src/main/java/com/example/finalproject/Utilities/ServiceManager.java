package com.example.finalproject.Utilities;

import com.example.finalproject.Models.Service;

import java.util.ArrayList;

public class ServiceManager {

    public static ArrayList<Service> getServices() {

        ArrayList<Service> services = new ArrayList<>();
        services.add(new Service()
                .setName("Haircut for Man")
                .setDescription("Professional haircutting and styling for men.")
                .setPrice("25$"));

        services.add(new Service()
                .setName("Haircut + Beard")
                .setDescription("Professional haircutting and styling for men + Beard design.")
                .setPrice("35$"));

        services.add(new Service()
                .setName("Beard Trim")
                .setDescription("Grooming and shaping of facial hair.")
                .setPrice("15$"));

        services.add(new Service()
                .setName("Soldier Haircut")
                .setDescription("Shaved haircut for soldier.")
                .setPrice("20$"));

        services.add(new Service()
                .setName("Haircut for Woman")
                .setDescription("Professional trimming and shaping of hair.")
                .setPrice("50$"));

        services.add(new Service()
                .setName("Hair Drying")
                .setDescription("low-drying and styling hair.")
                .setPrice("30$"));

        services.add(new Service()
                .setName("Scalp Massage")
                .setDescription("Relaxing massage to relieve tension and improve scalp health.")
                .setPrice("20$"));


        return services;
    };

}
