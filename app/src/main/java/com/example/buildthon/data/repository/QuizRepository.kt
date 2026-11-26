package com.example.buildthon.data.repository

import com.example.buildthon.data.model.QuizQuestion

class QuizRepository {
    fun getQuestions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                questionText = "What is the primary indicator of a secure website connection?",
                options = listOf(
                    "A green background color on the homepage",
                    "A padlock icon and 'https://' in the URL bar",
                    "A fast loading speed",
                    "Pop-up security banners"
                ),
                correctAnswerIndex = 1,
                explanation = "HTTPS and the padlock icon ensure that data transmitted between your browser and the website is encrypted and secure."
            ),
            QuizQuestion(
                id = 2,
                questionText = "What is a phishing attack?",
                options = listOf(
                    "A virus that deletes your hard drive",
                    "A fraudulent attempt to steal sensitive data by pretending to be a trusted entity",
                    "A technique to speed up slow internet connections",
                    "A secure method of backing up photos"
                ),
                correctAnswerIndex = 1,
                explanation = "Phishing attacks use deceptive emails or messages posing as trusted sources to trick you into revealing passwords or credit card numbers."
            ),
            QuizQuestion(
                id = 3,
                questionText = "Which of the following is considered a strong password?",
                options = listOf(
                    "12345678",
                    "password",
                    "Blue-Mountain-Coffee-42!",
                    "john_doe"
                ),
                correctAnswerIndex = 2,
                explanation = "Strong passwords use a combination of uppercase, lowercase, numbers, and symbols, and are typically longer than 12 characters."
            ),
            QuizQuestion(
                id = 4,
                questionText = "What does Two-Factor Authentication (2FA) do?",
                options = listOf(
                    "Doubles your internet download speed",
                    "Requires two passwords instead of one",
                    "Adds an extra layer of verification beyond just your password",
                    "Automatically blocks all emails"
                ),
                correctAnswerIndex = 2,
                explanation = "2FA requires two separate verification factors (e.g., password + SMS code or authenticator app) to secure your account."
            ),
            QuizQuestion(
                id = 5,
                questionText = "Why should you avoid using public Wi-Fi for sensitive tasks like banking?",
                options = listOf(
                    "Public Wi-Fi drains battery faster",
                    "Public networks are often unencrypted, allowing hackers to intercept your data",
                    "Banks block all public Wi-Fi connections",
                    "Public Wi-Fi has a data limit"
                ),
                correctAnswerIndex = 1,
                explanation = "Unencrypted public Wi-Fi can be monitored by attackers on the same network, making banking data vulnerable to interception."
            ),
            QuizQuestion(
                id = 6,
                questionText = "What is social engineering in cybersecurity?",
                options = listOf(
                    "Programming secure firewalls",
                    "Manipulating human psychology to trick people into giving up confidential info",
                    "Designing computer hardware",
                    "Fixing bugs in software code"
                ),
                correctAnswerIndex = 1,
                explanation = "Social engineering targets human trust and emotions (like fear or urgency) rather than technical security flaws."
            ),
            QuizQuestion(
                id = 7,
                questionText = "What should you do if you receive a suspicious email claiming your account is locked?",
                options = listOf(
                    "Click the link immediately and enter your password",
                    "Forward the email to all your friends",
                    "Ignore urgent links and navigate to the official website directly to verify",
                    "Reply with your password to unlock the account"
                ),
                correctAnswerIndex = 2,
                explanation = "Always navigate to official websites directly rather than clicking links in unexpected or urgent emails."
            ),
            QuizQuestion(
                id = 8,
                questionText = "What is ransomware?",
                options = listOf(
                    "Free software to clean malware",
                    "Malware that encrypts your files and demands payment to restore access",
                    "An app to manage passwords",
                    "A secure cloud storage backup"
                ),
                correctAnswerIndex = 1,
                explanation = "Ransomware locks or encrypts your files and holds them hostage until a ransom is paid (though paying is never guaranteed to work)."
            ),
            QuizQuestion(
                id = 9,
                questionText = "Why is software and operating system updating important?",
                options = listOf(
                    "It changes the wallpaper",
                    "It patches security vulnerabilities discovered by developers",
                    "It increases screen brightness",
                    "It deletes old photos to save space"
                ),
                correctAnswerIndex = 1,
                explanation = "Updates frequently include critical security patches that protect your device against newly discovered cyber threats."
            ),
            QuizQuestion(
                id = 10,
                questionText = "What is a password manager used for?",
                options = listOf(
                    "Sharing your passwords on social media",
                    "Generating and securely storing complex, unique passwords for every site",
                    "Guessing your friends' passwords",
                    "Printing your passwords on paper"
                ),
                correctAnswerIndex = 1,
                explanation = "Password managers generate and encrypt strong, unique passwords so you only need to remember one master password."
            )
        )
    }
}
