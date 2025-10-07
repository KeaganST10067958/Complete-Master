# Plan-demic — Motivational Study Companion App

## 👩‍💻 Developers

**Keagan Shaw** & **Paayal Rakesh**

---

## 🎯 Project Purpose

**Plan-Demic** is a productivity and motivation app designed to help students manage their academic goals effectively.

It combines a **Pomodoro timer**, **to-do list**, **sticky notes**, and a **motivational quote system** powered by a custom **REST API**.

The app helps users maintain focus, track daily progress, and stay inspired while studying — integrating psychology-based motivation with practical study tools.

---

## 🧠 Features Implemented

| Feature | Description | Status |
| :--- | :--- | :--- |
| **🔐 Single Sign-On (SSO)** | Login and registration using Firebase Authentication (Google Sign-In). | ✅ Implemented |
| **👤 User Account Management** | Keeps the user logged in until logout is selected. | ✅ Implemented |
| **⚙️ Settings Menu** | Allows light/dark mode toggle, shows logged-in user info, and logout option. | ✅ Implemented |
| **📝 Sticky Notes** | Create, edit, and delete personalized study notes. | ✅ Implemented |
| **✅ To-Do List** | Full CRUD functionality for managing daily tasks. | ✅ Implemented |
| **⏱️ Pomodoro Timer** | Focus timer with 25-minute study and 5-minute break intervals. | ✅ Implemented |
| **💬 Motivational Quotes API** | Custom REST API hosted on Render, displaying quotes from Firestore. | ✅ Implemented |
| **🌙 Dark/Light Mode** | Toggleable via the settings page. | ✅ Implemented |
| **💾 Offline Support** | Tasks and notes stored locally and persist between sessions. | ✅ Implemented |

---

## ☁️ REST API Creation & Integration

### 🛠️ API Creation

* **Built with:** `Node.js` + `Express.js` + `Firebase Functions`
* **Database:** `Firestore`
* **Hosting:** `Render` (Free-tier)
* **Collection:** `quotes`
* **Endpoints:**
    * `GET /quotes/random` → returns a random motivational quote
    * `POST /quotes` → allows admin to add new quotes

### 🔗 API Integration in App

Integrated using **Retrofit** in **Kotlin**.

The app fetches a random quote from the API and displays it on the **splash screen** for 3–5 seconds before loading the main dashboard.

**API URL:**

👉 `https://quotes-api-render.onrender.com/quotes/random`

**API Integration Process:**
<div align="center">
  <img src="https://github.com/user-attachments/assets/f4f3e695-f7c4-428c-9758-eb7bc7a9b3d6" alt="Login Screen" width="200" style="margin: 5px;"/>
  <img src="https://github.com/user-attachments/assets/7a1fbc6e-4139-4b99-9156-1736d8e3008c" alt="Splash Quote Screen" width="200" style="margin: 5px;"/>
  <img src="https://github.com/user-attachments/assets/19af5699-3ce0-4049-9f18-2c6ff91ce992" alt="To-Do List Screen" width="200" style="margin: 5px;"/>
  <img src="https://github.com/user-attachments/assets/cbe69e59-c13d-40b8-8463-5a0898426a89" alt="Sticky Notes Screen" width="200" style="margin: 5px;"/>
</div>


---

## 📸 Screenshots

| Screen | Description | Screenshot |
| :--- | :--- | :--- |
| **Login Screen** | Google SSO & Email Login | <img src="https://github.com/user-attachments/assets/54050633-de0f-44b9-9b53-5e364e31b3b5" width="150"/> <img src="https://github.com/user-attachments/assets/576f9c07-df52-4727-9071-a9f935f64553" width="150"/> <img src="https://github.com/user-attachments/assets/d5071728-d124-44a1-9aa6-21f1c42283c1" width="150"/>|
| **Home (Splash Quote)/ Dashboard** | Home displays random motivational quote and Dashboard displays navigation | <img src="https://github.com/user-attachments/assets/9bb07d47-2e6f-4017-836c-9ea9292985e0" width="150"/> <img src="https://github.com/user-attachments/assets/f538ef21-3316-4f31-9672-033d1e7707d7" width="150"/> <img src="https://github.com/user-attachments/assets/926b0494-07ac-4196-97a5-26b7d743e084" alt="Main App Dashboard" width="150"/>|
| **To-Do List** | Add/Edit/Delete daily tasks |<img src="https://github.com/user-attachments/assets/f02beb44-31be-40d0-a2dc-a9511b69fe91" width="150"/> <img src="https://github.com/user-attachments/assets/b7dc9d1a-6a41-4aa4-8887-9ac3b822ab15" width="150"/>|
| **Sticky Notes** | Manage study notes |  <img src="https://github.com/user-attachments/assets/b086a86f-d605-4c71-bb46-b00157555a01" width="150"/> <img src="https://github.com/user-attachments/assets/9e6b2798-1e89-46de-b157-66ff208e6a2c" width="150"/>|
| **Pomodoro Timer** | Study & break sessions |<img src="https://github.com/user-attachments/assets/5314dcab-e1d2-4068-90a2-79b3f335f929" width="150"/>|
| **Settings** | User info + theme toggle | <img src="https://github.com/user-attachments/assets/653a86e6-2e67-429f-b090-00b3b9b2bb54" width="150"/> |

---

## 🎥 Demonstration Video

**🎬 Demo Video Link:**

👉 **https://youtu.be/UOVcgdoP1DY?si=eYWomwWvBzFPzQq1**

---

## 📘 Research & Design Alignment

This project is based on research from the Planning & Design documents, comparing MyStudyLife, TickTick, and School Planner.

Findings from that research influenced these design choices:

* **Minimalistic UI:** To reduce distraction during study sessions.
* **Motivational System:** Based on the need for emotional reinforcement identified in student feedback studies.
* **Offline-first Approach:** Ensures usability even without network access.
* **RESTful API:** Adds personalization via motivational quotes and shows understanding of modern backend integration.

---

## 📄 Future Enhancements (for POE)

* 🗓️ Calendar integration for session scheduling
* ☁️ Cloud sync for notes and tasks
* 🔔 Smart reminders and notifications
* 📊 Study analytics dashboard

---

## 📚 References

* Firebase Documentation (Google, 2025)
* Render Deployment Docs (2025)
* Retrofit & Gson Libraries (Square, 2025)
* Android Developer Guides (Jetpack Compose & MVVM)

