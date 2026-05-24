
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

# 🖼️ Application Preview

## 👤 User Experience

<p align="center">
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 48" src="https://github.com/user-attachments/assets/38861be7-3d93-4962-95f6-6a95f3a504b6" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 47" src="https://github.com/user-attachments/assets/8c915030-cefa-42bb-90cd-816f2a387a0d" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 48 (1)" src="https://github.com/user-attachments/assets/c334c4cb-eb88-4a0c-9c96-575513ba9e76" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 48 (2)" src="https://github.com/user-attachments/assets/1c5e65c0-5e5f-4600-b5fc-5d6834e580f7" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 56" src="https://github.com/user-attachments/assets/f1912bd4-93f7-4f2d-8a16-a83bbc33456b" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 56 (2)" src="https://github.com/user-attachments/assets/dba3ed7b-d4d3-4add-940a-11d89f8c4203" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 49 (1)" src="https://github.com/user-attachments/assets/85a28121-5230-4503-9744-d202c2e1ee2e" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 49" src="https://github.com/user-attachments/assets/414efb6f-4859-4584-a38e-15be88bd2b2b" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 50 (1)" src="https://github.com/user-attachments/assets/5c3d0bc1-5162-4c77-a29c-76905b44d076" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 49 (2)" src="https://github.com/user-attachments/assets/86c3647b-79a7-43c4-83f0-7d3260bd199b" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 56 (1)" src="https://github.com/user-attachments/assets/6332313d-0f18-4596-8d4e-3f673d8d46b9" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 57" src="https://github.com/user-attachments/assets/806ec3e6-83a1-4820-9dca-8fbf4e830c36" />  
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 50" src="https://github.com/user-attachments/assets/5507021e-be42-46bc-9327-95bda9cb9f4b" />

</p>

<p align="center">

</p>

---

## 🛠️ Admin Experience

<p align="center">
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 50 (2)" src="https://github.com/user-attachments/assets/e4187a35-f273-4d1e-b5c7-bd625fe43588" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 50 (3)" src="https://github.com/user-attachments/assets/720df662-016d-4dcd-b682-004af0822435" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 55 (1)" src="https://github.com/user-attachments/assets/675961aa-dee4-4c96-96c9-20d662bb39e3" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 55 (2)" src="https://github.com/user-attachments/assets/83dbd127-37d1-4c5e-834c-59ca8cf9f9ab" />
<img width="220" alt="WhatsApp Image 2026-05-24 at 12 48 55" src="https://github.com/user-attachments/assets/06d8ee45-168c-486c-9a50-feb22770b72a" />
</p>

<p align="center">

</p>

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

## 🧑‍💼 Author
- **Denell Vandayar** 

Developers of the Kloofhill Caring Mobile App

---


