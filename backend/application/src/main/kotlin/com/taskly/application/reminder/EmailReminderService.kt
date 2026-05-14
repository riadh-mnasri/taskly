package com.taskly.application.reminder

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FR_DATE = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH)

@Service
class EmailReminderService(
    private val mailSender: JavaMailSender,
    @Value("\${taskly.mail.from:noreply@taskly.app}") private val from: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendReminder(task: TaskReminderRow) {
        try {
            val msg = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(msg, true, "UTF-8")
            helper.setFrom(from)
            helper.setTo(task.userEmail)
            helper.setSubject("⏰ Rappel Taskly — ${task.title} est dû demain")
            helper.setText(buildHtml(task), true)
            mailSender.send(msg)
            log.info("Reminder sent to ${task.userEmail} for task ${task.taskId}")
        } catch (e: Exception) {
            log.error("Failed to send reminder for task ${task.taskId}: ${e.message}")
        }
    }

    private fun buildHtml(task: TaskReminderRow): String {
        val priorityLabel = when (task.priority) {
            "HIGH"   -> "🔴 Haute"
            "MEDIUM" -> "🟠 Moyenne"
            else     -> "🟢 Basse"
        }
        val dateStr = task.dueDate.format(FR_DATE)
            .replaceFirstChar { it.uppercase() }

        return """
        <!DOCTYPE html>
        <html lang="fr">
        <head><meta charset="UTF-8"/></head>
        <body style="margin:0;padding:0;background:#f5f3ff;font-family:Arial,sans-serif;">
          <table width="100%" cellpadding="0" cellspacing="0">
            <tr><td align="center" style="padding:32px 16px;">
              <table width="560" cellpadding="0" cellspacing="0"
                     style="background:white;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">

                <!-- Header -->
                <tr>
                  <td style="background:linear-gradient(135deg,#4c1d95,#7c3aed);padding:28px 32px;">
                    <div style="font-size:28px;margin-bottom:6px;">📚 Taskly</div>
                    <div style="color:rgba(255,255,255,0.85);font-size:14px;">Ton espace scolaire</div>
                  </td>
                </tr>

                <!-- Body -->
                <tr>
                  <td style="padding:32px;">
                    <p style="font-size:18px;font-weight:700;color:#1f2937;margin:0 0 8px;">
                      ⏰ Rappel — échéance demain
                    </p>
                    <p style="color:#6b7280;font-size:14px;margin:0 0 24px;">
                      La tâche suivante est due le <strong>${dateStr}</strong>.
                    </p>

                    <!-- Task card -->
                    <div style="background:#f5f3ff;border-radius:12px;padding:20px;border-left:4px solid #7c3aed;">
                      <div style="font-size:17px;font-weight:700;color:#1f2937;margin-bottom:6px;">
                        ${task.title}
                      </div>
                      <div style="font-size:13px;color:#6b7280;">
                        📖 ${task.subject} &nbsp;·&nbsp; Priorité : $priorityLabel
                      </div>
                    </div>

                    <p style="margin:24px 0 0;color:#6b7280;font-size:13px;text-align:center;">
                      Ouvre Taskly pour marquer cette tâche comme terminée.
                    </p>
                  </td>
                </tr>

                <!-- Footer -->
                <tr>
                  <td style="background:#f9fafb;padding:16px 32px;text-align:center;">
                    <p style="color:#9ca3af;font-size:11px;margin:0;">
                      © ${java.time.Year.now().value} WeHighTech · Taskly
                    </p>
                  </td>
                </tr>

              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.trimIndent()
    }
}
