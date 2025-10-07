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
<img width="1904" height="936" alt="Screenshot 2025-10-07 200411" src="https://github.com/user-attachments/assets/f4f3e695-f7c4-428c-9758-eb7bc7a9b3d6" />
<img width="1919" height="939" alt="Screenshot 2025-10-07 200426" src="https://github.com/user-attachments/assets/7a1fbc6e-4139-4b99-9156-1736d8e3008c" />
<img width="1542" height="707" alt="Screenshot 2025-10-07 201445" src="https://github.com/user-attachments/assets/19af5699-3ce0-4049-9f18-2c6ff91ce992" />
<img width="1917" height="933" alt="Screenshot 2025-10-07 202318" src="https://github.com/user-attachments/assets/cbe69e59-c13d-40b8-8463-5a0898426a89" />


---

## 📸 Screenshots

| Screen | Description | Screenshot |
| :--- | :--- | :--- |
| **Login Screen** | Google SSO & Email Login | 📷 (Add your own screenshots here once you’ve taken them) |
| **Home (Splash Quote)** | Displays random motivational quote | 📷 (Add your own screenshots here once you’ve taken them) |
| **To-Do List** | Add/Edit/Delete daily tasks | 📷 (Add your own screenshots here once you’ve taken them) |
| **Sticky Notes** | Manage study notes | 📷 (Add your own screenshots here once you’ve taken them) |
| **Pomodoro Timer** | Study & break sessions | 📷 (Add your own screenshots here once you’ve taken them) |
| **Settings** | User info + theme toggle | 📷 (Add your own screenshots here once you’ve taken them) |

---

## 🎥 Demonstration Video

**🎬 Demo Video Link:**

👉 **[Insert YouTube or Google Drive Link Here]**

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

