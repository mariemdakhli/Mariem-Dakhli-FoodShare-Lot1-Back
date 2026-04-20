package com.microservice.foodsharepi.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Envoie l'email de bienvenue avec lien de création de mot de passe
     */
    public void sendWelcomeAndSetPasswordEmail(String to, String firstName, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🎉 Bienvenue sur FoodShare - Créez votre mot de passe");

            String setPasswordLink = frontendUrl + "/create-password?token=" + token;
            String loginLink = frontendUrl + "/login";

            // Template HTML intégré directement
            String htmlContent = buildEmailTemplate(firstName, to, setPasswordLink, loginLink);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Email de bienvenue envoyé à : " + to);

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        }
    }

    /**
     * Construit le template HTML de l'email
     */
    private String buildEmailTemplate(String firstName, String to, String setPasswordLink, String loginLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <style>" +
                "        body {" +
                "            font-family: 'Segoe UI', Arial, sans-serif;" +
                "            line-height: 1.6;" +
                "            color: #333;" +
                "            max-width: 600px;" +
                "            margin: 0 auto;" +
                "            padding: 20px;" +
                "            background: #f5f7fa;" +
                "        }" +
                "        .email-container {" +
                "            background: white;" +
                "            border-radius: 16px;" +
                "            overflow: hidden;" +
                "            box-shadow: 0 4px 20px rgba(0,0,0,0.1);" +
                "        }" +
                "        .header {" +
                "            background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%);" +
                "            color: white;" +
                "            padding: 40px 30px;" +
                "            text-align: center;" +
                "        }" +
                "        .header h1 {" +
                "            margin: 0;" +
                "            font-size: 32px;" +
                "        }" +
                "        .header p {" +
                "            margin: 10px 0 0;" +
                "            opacity: 0.9;" +
                "        }" +
                "        .content {" +
                "            padding: 30px;" +
                "        }" +
                "        .welcome-message {" +
                "            font-size: 18px;" +
                "            margin-bottom: 20px;" +
                "        }" +
                "        .button {" +
                "            display: inline-block;" +
                "            padding: 15px 35px;" +
                "            background: #2ecc71;" +
                "            color: white;" +
                "            text-decoration: none;" +
                "            border-radius: 30px;" +
                "            margin: 25px 0;" +
                "            font-weight: bold;" +
                "            font-size: 16px;" +
                "        }" +
                "        .info-box {" +
                "            background: #f0fdf4;" +
                "            padding: 20px;" +
                "            border-radius: 12px;" +
                "            margin: 25px 0;" +
                "            border-left: 4px solid #2ecc71;" +
                "        }" +
                "        .features {" +
                "            display: grid;" +
                "            grid-template-columns: repeat(2, 1fr);" +
                "            gap: 15px;" +
                "            margin: 25px 0;" +
                "        }" +
                "        .feature-item {" +
                "            display: flex;" +
                "            align-items: center;" +
                "            gap: 10px;" +
                "        }" +
                "        .feature-icon {" +
                "            width: 40px;" +
                "            height: 40px;" +
                "            background: #e8f5e9;" +
                "            border-radius: 50%;" +
                "            display: flex;" +
                "            align-items: center;" +
                "            justify-content: center;" +
                "            font-size: 20px;" +
                "        }" +
                "        .footer {" +
                "            margin-top: 30px;" +
                "            padding-top: 20px;" +
                "            border-top: 1px solid #eee;" +
                "            text-align: center;" +
                "            color: #7f8c8d;" +
                "            font-size: 13px;" +
                "        }" +
                "        .link-box {" +
                "            background: #f8f9fa;" +
                "            padding: 15px;" +
                "            border-radius: 8px;" +
                "            word-break: break-all;" +
                "            font-family: monospace;" +
                "            font-size: 13px;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"email-container\">" +
                "        <div class=\"header\">" +
                "            <h1>🍽️ FoodShare</h1>" +
                "            <p>Bienvenue dans la communauté anti-gaspillage !</p>" +
                "        </div>" +
                "        " +
                "        <div class=\"content\">" +
                "            <div class=\"welcome-message\">" +
                "                <strong>Bonjour " + escapeHtml(firstName) + " !</strong> 👋" +
                "            </div>" +
                "            " +
                "            <p>Merci de vous être inscrit(e) comme bénévole sur FoodShare. Votre engagement va nous aider à réduire le gaspillage alimentaire et à aider ceux qui en ont besoin.</p>" +
                "            " +
                "            <div class=\"info-box\">" +
                "                <strong>📧 Votre email de connexion :</strong><br>" +
                "                <span style=\"font-size: 16px;\">" + escapeHtml(to) + "</span>" +
                "            </div>" +
                "            " +
                "            <p><strong>Pour commencer, vous devez créer votre mot de passe :</strong></p>" +
                "            " +
                "            <center>" +
                "                <a href=\"" + setPasswordLink + "\" class=\"button\">" +
                "                    🔐 Créer mon mot de passe" +
                "                </a>" +
                "            </center>" +
                "            " +
                "            <p><small>⏰ Ce lien est valable 24 heures.</small></p>" +
                "            " +
                "            <div class=\"link-box\">" +
                "                <small>Si le bouton ne fonctionne pas, copiez ce lien :</small><br>" +
                "                <span>" + setPasswordLink + "</span>" +
                "            </div>" +
                "            " +
                "            <hr style=\"margin: 30px 0; border: 1px solid #eee;\">" +
                "            " +
                "            <h3>🌟 Ce qui vous attend sur FoodShare :</h3>" +
                "            " +
                "            <div class=\"features\">" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">📋</div>" +
                "                    <span>Missions variées</span>" +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">⭐</div>" +
                "                    <span>Gagnez des points</span>" +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">🏅</div>" +
                "                    <span>Débloquez des badges</span>" +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">📊</div>" +
                "                    <span>Suivez votre impact</span>" +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">🤝</div>" +
                "                    <span>Communauté solidaire</span>" +
                "                </div>" +
                "                <div class=\"feature-item\">" +
                "                    <div class=\"feature-icon\">🌍</div>" +
                "                    <span>Agissez pour la planète</span>" +
                "                </div>" +
                "            </div>" +
                "            " +
                "            <div class=\"footer\">" +
                "                <p>© 2026 FoodShare - Tous droits réservés</p>" +
                "                <p>" +
                "                    <a href=\"" + loginLink + "\" style=\"color: #2ecc71;\">Se connecter</a> | " +
                "                    <a href=\"#\" style=\"color: #2ecc71;\">Centre d'aide</a>" +
                "                </p>" +
                "                <p style=\"font-size: 12px;\">Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "            </div>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Échappe les caractères HTML pour éviter les injections
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * ✅ Envoyer un email simple (texte brut)
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("✅ Email simple envoyé à : " + to);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        }
    }

}