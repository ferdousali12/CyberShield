package com.example.cybershield.data.repository

import com.example.cybershield.data.model.QuizQuestion

class QuizRepository {
    fun getQuestions(): List<QuizQuestion> {
        return listOf(
            // 1. Cybersecurity fundamentals
            QuizQuestion(
                id = 1,
                questionText = "What is the primary goal of keeping your software and operating system updated?",
                options = listOf(
                    "Changing the home screen wallpaper",
                    "Patching security vulnerabilities discovered by developers",
                    "Increasing screen brightness",
                    "Clearing storage space"
                ),
                correctAnswerIndex = 1,
                explanation = "Software updates frequently include critical security patches that protect your device against newly discovered exploits."
            ),
            // 2. Phishing and malicious URLs
            QuizQuestion(
                id = 2,
                questionText = "Which of the following URL structures indicates a potential phishing threat?",
                options = listOf(
                    "https://www.github.com/login",
                    "https://support.google.com",
                    "http://paypa1-secure-login.xyz/auth",
                    "https://developer.android.com"
                ),
                correctAnswerIndex = 2,
                explanation = "Typosquatting (like 'paypa1' with a number 1), unencrypted HTTP, and suspicious .xyz TLDs are classic indicators of phishing."
            ),
            // 3. Password security
            QuizQuestion(
                id = 3,
                questionText = "Which of the following practices creates the strongest password security?",
                options = listOf(
                    "Using your pet's name and birth year",
                    "Using the exact same complex password across all accounts",
                    "Using a password manager to generate and store unique, high-entropy passwords",
                    "Writing passwords on a sticky note near your desk"
                ),
                correctAnswerIndex = 2,
                explanation = "Password managers allow you to use unique, complex passwords for every service without needing to memorize them all."
            ),
            // 4. Two-factor authentication
            QuizQuestion(
                id = 4,
                questionText = "What makes Two-Factor Authentication (2FA) significantly safer than a password alone?",
                options = listOf(
                    "It doubles your internet connection speed",
                    "It requires a second independent verification factor (e.g., authenticator app or hardware key)",
                    "It disables account login entirely",
                    "It automatically logs you out every minute"
                ),
                correctAnswerIndex = 1,
                explanation = "Even if a hacker steals your password, 2FA blocks access because they lack the secondary verification factor."
            ),
            // 5. Online privacy
            QuizQuestion(
                id = 5,
                questionText = "Why should you be cautious when granting location permissions to mobile apps?",
                options = listOf(
                    "It drains device storage space",
                    "It reveals your physical movements and sensitive habits to trackers",
                    "It slows down Bluetooth connectivity",
                    "It automatically locks your device"
                ),
                correctAnswerIndex = 1,
                explanation = "Unnecessary location permissions allow apps and third-party trackers to build detailed profiles of your daily routine."
            ),
            // 6. Social engineering
            QuizQuestion(
                id = 6,
                questionText = "What is CEO Fraud (Business Email Compromise)?",
                options = listOf(
                    "A virus that crashes company servers",
                    "An attacker impersonating an executive in urgent messages to manipulate employees into wiring funds",
                    "A tool for auditing company code",
                    "A method for speeding up payroll"
                ),
                correctAnswerIndex = 1,
                explanation = "CEO fraud exploits authority and urgency to manipulate employees into bypassing security controls for financial transfers."
            ),
            // 7. Safe browsing
            QuizQuestion(
                id = 7,
                questionText = "What does the 'HTTPS' protocol guarantee when browsing a website?",
                options = listOf(
                    "The website is 100% scam-free",
                    "Data transmitted between your browser and the website is encrypted",
                    "The website loads instantly",
                    "No advertisements will appear"
                ),
                correctAnswerIndex = 1,
                explanation = "HTTPS encrypts transit data, protecting you from eavesdropping and man-in-the-middle attacks on the network."
            ),
            // 8. Public Wi-Fi safety
            QuizQuestion(
                id = 8,
                questionText = "What is the primary risk of conducting banking on an open public Wi-Fi network?",
                options = listOf(
                    "Your phone battery drains faster",
                    "Attackers on the same network can intercept unencrypted traffic (packet sniffing)",
                    "Banks automatically ban public IP addresses",
                    "Data downloads are capped"
                ),
                correctAnswerIndex = 1,
                explanation = "Open public networks lack encryption, allowing malicious actors on the network to intercept sensitive credentials."
            ),
            // 9. QR code scams
            QuizQuestion(
                id = 9,
                questionText = "What is 'Quishing' (QR code phishing)?",
                options = listOf(
                    "A fast way to recharge your phone battery",
                    "Placing malicious QR codes in public places that redirect users to credential-harvesting sites",
                    "A file compression algorithm",
                    "A method to speed up camera focus"
                ),
                correctAnswerIndex = 1,
                explanation = "Quishing involves sticking fraudulent QR codes over legitimate parking meters or flyers to trick users into visiting fake payment portals."
            ),
            // 10. SMS and online scams
            QuizQuestion(
                id = 10,
                questionText = "What should you do if you receive an unexpected SMS claiming you won a lottery?",
                options = listOf(
                    "Click the link immediately to claim your money",
                    "Ignore and delete the message; unsolicited lottery wins are always scams",
                    "Reply with your home address",
                    "Call the number back to verify"
                ),
                correctAnswerIndex = 1,
                explanation = "If you didn't enter a lottery, you cannot win one. Unsolicited winning notices are classic SMS lures."
            )
        )
    }
}
