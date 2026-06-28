# DECYRA: AI-Powered Academic and Career Guidance App

You can watch the Demo Video [here](https://www.youtube.com/watch?v=UF1DgyN0P_4).  
Explore our UI [here](https://miro.com/app/board/uXjVHBeWLB0=/?share_link_id=507636727005).

## Overview

**DECYRA** is a production-ready android mobile application designed to help students make better academic and career decisions in the field of Computer Science. It combines AI-powered guidance with community-driven interaction, offering personalized support for Erasmus choices, Master studies, and early career planning.

Many students struggle to decide between options such as joining an Erasmus program, applying for a Master's degree, or entering the job market. **DECYRA** was created to make this process easier through an intelligent mentor system, real-time communication features, and a modern mobile interface.

The app does not focus only on generic recommendations. Instead, it aims to provide more personalized guidance based on user needs, preferences, and goals, while also allowing students to exchange experiences through community features.

## Main Features

- AI mentor for academic and career guidance
- User authentication with Face ID support
- Forum for sharing opinions and experiences
- P2P chat between users
- Video conferencing for live communication
- Notes system for saving useful results, meeting codes, and personal reminders
- Speech-to-text and Text-to-speech support for easier interaction
- Responsive and modern mobile UI built for usability
- As in most production-ready applications, users can choose to stay signed in. When the **Remember Me** option is enabled, **DECYRA** automatically restores the user's session, eliminating the need to log in again on subsequent launches.

## Technology Stack

**DECYRA** is a multi-technology mobile application that combines Android development, cloud services, AI services, and real-time communication tools.

### Mobile Development
- **Kotlin** with **Jetpack Compose Framework** for modern Android frontend and UI development.
- **Java** for backend and core application logic

### NLP
- **OpenAI GPT-4.1** for final response generation
- **OpenAI GPT-4o-mini** for lightweight classification tasks
- **OpenAI text-embedding-3-small** for text embeddings
- **Pinecone** as the vector database for semantic retrieval
- **Cohere Rerank** for improving result relevance
- **RAG (Retrieval-Augmented Generation)** architecture for personalized recommendations
- Learn more about our [AI Agent Architecture](app/src/main/java/com/example/decyra/backend/ai/README.md).

### Computer Vision and Augmented Reality (AR)
- **Google ML Kit** for face detection
- **FaceNet** for facial embedding generation and face-based login
- **AR guidance** to help users capture correct face angles during registration and sign-in
- Learn more about our [Computer Vision & AR](app/src/main/java/com/example/decyra/frontend/Computer_Vision-AR.md).

### Cloud and Backend Infrastructure
- **Firebase Realtime Database** as the main cloud database
- **Firebase Authentication** for user login and password reset
- **Firebase Cloud Messaging** for notifications
- **Supabase Edge Functions** for handling messaging-related requests
- **Supabase Storage** for profile image storage
- **MySQL** for our [database schema](db_schema/README.md)
- **HTTP** as the protocol we use for our server-client communications 

### Security
- **AWS Lambda** for secure API handling
- **AWS API Gateway** for request routing
- **AWS Secrets Manager** for secure API key storage
  
### Video Conference
- **WebRTC** as the protocol to connect users for a Video Conference through **ZEGOCLOUD API**.

### Additional Services
- **Google Services** for email-based password reset and speech-to-text , text-to-speech support

  
<p align="center">
  <img width="425" height="244" alt="image" src="https://github.com/user-attachments/assets/121c4413-1d27-41e0-a07b-438d103cf261" />
</p>

Read the full [documentation](app/src/main/java/com/example/decyra/frontend/README.md) for Cloud and Backend Infrastructure, Security, Video Conference and Additional Services.

## Project Structure

The project is organized into three main parts:

- **backend**: AI logic, domain model
- **frontend**: screen-level UI implementation with Java/Kotlin integration
- **extras**: utility classes and additional helper functionality

The application also includes cloud functions for secure API access and push notification handling.

## Why This Stack?

This technology stack enables scalability, real-time performance, and a modern mobile user experience. By combining native Android development with managed cloud services and cloud-based AI, DECYRA provides a lightweight platform designed for 24/7 availability, allowing users to access its services anytime without installing or maintaining additional software. In contrast, self-hosted solutions based on local LLMs (e.g., LLaMA) and self-managed Flask servers require dedicated hardware that must remain continuously powered, configured, and maintained to provide uninterrupted service. They also demand significantly more computational resources and storage on the host machine, and their performance depends on the capabilities of the local hardware. By using a **cloud-based architecture**, DECYRA delivers fast and consistent response times while eliminating the overhead of running resource-heavy models locally. Overall, this cloud-native architecture provides a scalable, centrally managed, and highly available platform with low operational overhead and reliable performance.

## Codebase Summary

The codebase includes a total of 100 files:

- 48 Java files
- 43 Kotlin files
- 6 interface files
- 2 cloud function files
- 1 enum file

## Evaluation Highlights

The project was evaluated through user research, expert feedback, and usability testing. Results showed strong interest in an app of this type, especially for the AI mentor and forum features, while usability scores were high for chatbot performance, registration flow, and navigation.

## Academic Context

Department of Informatics, Athens University of Economics and Business  
Course: Human-Computer Interaction  
Professor: A. A. Vasilakis  
Spring Semester 2025-26

## Developers

> [Nikolaos Poulopoulos](https://github.com/nikwilldoit), BSc Student  
> Department of Informatics, Athens University of Economics and Business  

> [Michail Marakis](https://github.com/Michail-Marakis), BSc Student  
> Department of Informatics, Athens University of Economics and Business  
