from django.db import models

class Run(models.Model):
    testName = models.CharField(max_length=255)
    timeStart = models.DateTimeField()
    timeEnd = models.DateTimeField()
    duration = models.FloatField()

    def __str__(self):
        return f"{self.testName} ({self.timeStart} - {self.timeEnd})"
