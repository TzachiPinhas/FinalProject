# Barber Book

## Overview
Barber Book is an Android application designed to streamline the process of booking appointments for haircuts. This application enables customers to register and schedule appointments based on real-time availability, utilizing Firebase for secure data handling and authentication.

## Features

### General
- **Firebase Integration**: All data and appointments are securely saved and managed in real-time via Firebase.
- **Dual Account Types**: Supports two distinct types of user accounts—Customer and Hairstylist, each with tailored functionalities.

### For Customers
- **Diverse Login Options**: Users can sign in with an email and password, receive an SMS message to their mobile phone, or sign in with their Gmail account.
- **Home page**: The home page always shows the future meeting if it exists, the general rating of the hairdresser and some of the opinions.
- **Appointment Booking**: Customers can view the menu detailing all the services offered by the barber shop. They can choose a specific service in which they are interested, view the - -dates and times that are available, and order at the most convenient time for them.
- **Appointment Management**: Customers will be able to view their appointments (past and future appointments) allowing users to easily manage their schedule.
- **Appointment Cancellation**: Customers have the option to cancel appointments at any moment, easily without having to call the hairdresser.
- **No Double Booking**: The system ensures that customers cannot book more than one appointment in the same time slot.
- **Reviews and Ratings**: Customers can view and post reviews and ratings for the services received.

### For Hairstylists
- **Appointment Management**: Hairstylists can view all past and future appointments and access customer details. They have the ability to filter appointments by date.
- **Availability Management**: Hairstylists can block out days when they are not available, provided no appointments are already scheduled for those days.

### Additional Features
- **Navigation and Communication**: The 'About Us' page includes options to navigate to the hair salon or call the business owner directly, enhancing convenience and accessibility.


## Configuration

- Ensure you have a Firebase account and the project is linked to Firebase.


## Future Improvements

The following are some of the enhancements planned for future versions of Barber Book:

1. **Time Selector Improvements**: Refine the time selector to handle edge cases and finite hours more effectively, ensuring that appointment times are always within business hours and avoid overlaps.

2. **Multi-Hairstylist Support**: Expand the application to support multiple hairstylists. This will allow customers to choose their preferred hairstylist and view specific availability per hairstylist.

3. **Push Notifications**: Add push notifications to remind customers of upcoming appointments and notify them of any changes in their appointment status or special promotions.

4. **Multi-Language Support**: Expand the app to support multiple languages, making it accessible to a broader audience.


## Technology Stack

Barber Book is built with a range of modern technologies ensuring robust and scalable performance. Here's a breakdown of the main components:

- **Frontend**: 
  - **Android Studio**: Used for the entire application development, providing a powerful environment for building Android apps.
  - **XML**: Utilized for designing the layout of the application.
  - **MaterialDateTimePicker by wdullaer**: An external library used to provide a user-friendly date and time picker. This enhances the UI/UX for scheduling appointments. [More on GitHub](https://github.com/wdullaer/MaterialDateTimePicker)

- **Backend**:
  - **Firebase Realtime Database**: Serves as the backend database to store all application data including user profiles, appointment details, and reviews in real time.
  - **Firebase Authentication**: Manages user authentication processes, supporting email and password login, SMS verification, and Google authentication.


## Application Flow

Barber Book streamlines the process of scheduling and managing appointments for both customers and hairstylists. Below is an outline of the typical user interactions within the app:

### For Customers

1. **Registration/Login**: Customers begin by registering for an account using an email and password, SMS verification, or through their Google account.
![Registration/Login](https://github.com/TzachiPinhas/FinalProject/assets/141555220/88fa95f2-18ad-4246-882f-4b8ac3aa1b81)
2. **Home Page**: Upon successful login, the home page displays upcoming appointments. Customers can navigate through different sections via the menu.
![image](https://github.com/TzachiPinhas/FinalProject/assets/141555220/a2dabbd0-d5df-4e97-b9ae-68538f1c6508)
3. **Booking Appointments**: Customers select the 'Book Appointment' option where they can view a list of services offered. They select a service to see available dates and times, and book an appointment.
  ![image](https://github.com/TzachiPinhas/FinalProject/assets/141555220/36cac746-3b62-4233-95d7-ce29ddc1195c)
4. **Managing Appointments**: In the appointments section, customers can view, reschedule, or cancel their future and past appointments.
   ![image](https://github.com/TzachiPinhas/FinalProject/assets/141555220/26bf8aed-e9a9-401a-807e-7a875121ab7c)

6. **Reviews and Ratings**: After an appointment, customers can leave reviews and rate the service they received, which can be viewed by other users.
   ![image](https://github.com/TzachiPinhas/FinalProject/assets/141555220/52898374-99c4-4663-b48b-fa38ffc7f332)


### For Hairstylists

1. **View Appointments**: The main dashboard displays all upcoming and past appointments. Hairstylists can filter these appointments by date. For each appointment, hairstylists can view detailed information about the customer
![image](https://github.com/TzachiPinhas/FinalProject/assets/141555220/41bde73c-1da1-44af-90a9-0c086b69b1d3)
2. **Manage Availability**: Hairstylists can access their calendar to block out days they are unavailable if no appointments are scheduled.
   ![image](https://github.com/TzachiPinhas/FinalProject/assets/141555220/be6e7e1b-9639-44ae-8f3d-56a33a0e4363)

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests.


