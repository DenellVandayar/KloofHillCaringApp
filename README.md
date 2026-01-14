
# 🏥 Kloofhill Caring Mobile Application

The GitHub repository used for the development of the Kloofhill Caring mobile app

---

## 📚 Table of Contents
1. [Overview](#-overview)  
2. [Features](#-features)  
   - [User Features](#-user-features)  
   - [Admin Features](#-admin-features)  
3. [Tech Stack](#-tech-stack)  
4. [System Architecture](#-system-architecture)  
5. [Installation & Setup](#-installation--setup)  
6. [Authors](#-authors)

---

## 📱 Overview
The **Kloofhill Caring** mobile application was developed to support the operations of *Kloofhill Caring*, a nonprofit organization dedicated to hiring out medical equipment at fair prices to those in need, while also offering advice and support in general. The app provides a modern, user-friendly interface for people to **reserve medical items**, **make donations**, and **contact the organization** directly through the app.  

Administrators are provided with special privileges and different pages to **add equipment**, **edit equipment**, **delete equipment**,  **manage/view reservations**, **view submitted messages**, and **view recent donations** in real time.

---

## 🚀 Features

### 👤 User Features
- **User Authentication** – Secure login and registration using Firebase Authentication.  
- **Reserve Items** – Users can browse available medical equipment and reserve items to collect.  
- **Donations** – Users can contribute using the **Yoco Checkout API**, which provides a secure payment interface for online donations.  
- **Contact Us Page** – A simple form where users can submit their **details and a message** directly to the organization.  
- **Location Access** – Integration with **Google Maps** and **OSMDroid API** to allow users to find the organization’s location or get a rough idea where they are located.  
- **View Info About Kloofhill caring** - Users can view supported charities and the about us page simply from the home page to understand Kloofhill Caring better.
- **View and edit their profile** - A profile page is given to the users where they can see their name, email, address, phone number. These details can be edited at any time.

### 🛠️ Admin Features
- **Add, edit, delete equipment** – Admins can view all of the current equipment that is shown to users, and they are able to add new equipment, edit current equipment details and delete equipment from the list at any given time.
- **View Reservations** – Admins can view and manage all submitted reservations.  
- **View Donations** – Full visibility into user donations processed through Yoco Checkout.  
- **View Submitted Messages** – Admins can review and manage contact form submissions for better communication management.  

---

## 🧩 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Frontend** | Android (Kotlin) |
| **Backend** | Firebase Firestore, Firebase Authentication |
| **Payment Gateway** | Yoco Checkout API |
| **Mapping APIs** | Google Maps API, OSMDroid API |
| **Development Environment** | Android Studio |
| **Architecture Pattern** | Model-View-ViewModel (MVVM) |

---

## 🧭 System Architecture

The mobile app interacts directly with Firebase services to ensure **real-time synchronization**, **secure authentication**, and **reliable data storage**.  
- **Firebase Authentication** handles login and registration.  
- **Firebase Firestore** stores user, reservation, donation, contact messages, and user profiles.  
- **Yoco Checkout API** securely manages donation payments.  
- **Google Maps External Service and OSMDroid APIs** enhance the location and navigation experience.

---

## ⚙️ Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/YourUsername/KloofhillCaringApp.git
   cd KloofhillCaringApp
   ```

2. **Open in Android Studio**
   - Open the project folder in Android Studio.
   - Let Gradle sync all dependencies.

3. **Configure Firebase**
   - Add your `google-services.json` file under the `app/` directory.
   - Ensure that Firebase Authentication and Firestore are enabled in the Firebase Console.

4. **Configure Yoco Checkout**
   - Add your Yoco public and secret keys in the designated configuration file or environment variables.

5. **Run the Application**
   - Connect an Android device or use an emulator.
   - Click **Run ▶️** in Android Studio.

---

## 🧑‍💼 Authors

- **Cade Gamble - ST10262209**
- **Denell Vandayar - ST10373357** 
- **Reece Corbett - ST10279058** 
- **Azande Mnguni - ST10365275** 
- **Kiera Meth - ST10279488** 
 
Developers of the Kloofhill Caring Mobile App

---


