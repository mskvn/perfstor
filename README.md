# perfstor README

## Установка библиотек

```bash
python -m venv venv
source venv/bin/activate
pip install django djangorestframework
```

## Cоздание проекта и настройка

```bash
django-admin startproject perfstor .
python manage.py startapp runs
```

В `perfstor/settings.py` добавьте в `INSTALLED_APPS`:

```python
INSTALLED_APPS = [
    ...
    'rest_framework',
    'runs',
]
```

## Создание Модели Run

В `runs/models.py`:

```python
from django.db import models

class Run(models.Model):
    testName = models.CharField(max_length=255)
    timeStart = models.DateTimeField()
    timeEnd = models.DateTimeField()
    duration = models.FloatField()

    def __str__(self):
        return f"{self.testName} ({self.timeStart} - {self.timeEnd})"
```

## Генерация миграции

```bash
python manage.py makemigrations
python manage.py migrate
```

## Создание Views

В `runs/forms.py`:

```python
from django import forms
from .models import Run

class RunForm(forms.ModelForm):
    class Meta:
        model = Run
        fields = '__all__'
        widgets = {
            'timeStart': forms.DateTimeInput(attrs={'type': 'datetime-local'}),
            'timeEnd': forms.DateTimeInput(attrs={'type': 'datetime-local'}),
        }
```

В `runs/views.py`:

```python
from django.shortcuts import render, redirect, get_object_or_404
from .models import Run
from .forms import RunForm
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status

# UI Views

def run_list(request):
    runs = Run.objects.all()
    return render(request, 'runs/run_list.html', {'runs': runs})

def run_create(request):
    if request.method == 'POST':
        form = RunForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect('run_list')
    else:
        form = RunForm()
    return render(request, 'runs/run_form.html', {'form': form})

def run_update(request, pk):
    run = get_object_or_404(Run, pk=pk)
    form = RunForm(request.POST or None, instance=run)
    if form.is_valid():
        form.save()
        return redirect('run_list')
    return render(request, 'runs/run_form.html', {'form': form})

def run_delete(request, pk):
    run = get_object_or_404(Run, pk=pk)
    if request.method == 'POST':
        run.delete()
        return redirect('run_list')
    return render(request, 'runs/run_confirm_delete.html', {'run': run})

def run_report(request, pk):
    run = get_object_or_404(Run, pk=pk)
    return render(request, 'runs/run_report.html', {'run': run})

# API

@api_view(['POST'])
def create_run_api(request):
    try:
        data = request.data
        run = Run.objects.create(
            testName=data['testName'],
            timeStart=data['timeStart'],
            timeEnd=data['timeEnd'],
            duration=data['duration']
        )
        return Response({'id': run.id}, status=status.HTTP_201_CREATED)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)
```

## Маршруты

В `runs/urls.py`:

```python
from django.urls import path
from . import views

urlpatterns = [
    path('', views.run_list, name='run_list'),
    path('create/', views.run_create, name='run_create'),
    path('update/<int:pk>/', views.run_update, name='run_update'),
    path('delete/<int:pk>/', views.run_delete, name='run_delete'),
    path('report/<int:pk>/', views.run_report, name='run_report'),
    path('api/run/', views.create_run_api, name='create_run_api'),
]
```

В `perfstor/urls.py` добавьте:

```python
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('runs.urls')),
]
```

## Создание Шаблонов

Создайте папку `runs/templates/runs/` и добавьте шаблоны:

**base.html**
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>perfstor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">
    {% block content %}{% endblock %}
</body>
</html>
```

**run_list.html**
```html
{% extends 'runs/base.html' %}

{% block content %}
<h1>Runs</h1>
<a href="/create/" class="btn btn-primary mb-3">Create New Run</a>
<table class="table table-bordered">
    <thead>
        <tr>
            <th>ID</th>
            <th>Test Name</th>
            <th>Start</th>
            <th>End</th>
            <th>Duration</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        {% for run in runs %}
        <tr>
            <td>{{ run.id }}</td>
            <td>{{ run.testName }}</td>
            <td>{{ run.timeStart }}</td>
            <td>{{ run.timeEnd }}</td>
            <td>{{ run.duration }}</td>
            <td>
                <a href="/update/{{ run.id }}/" class="btn btn-sm btn-warning">Edit</a>
                <a href="/delete/{{ run.id }}/" class="btn btn-sm btn-danger">Delete</a>
            </td>
        </tr>
        {% endfor %}
    </tbody>
</table>
{% endblock %}
```

**run_form.html**
```html
{% extends 'runs/base.html' %}

{% block content %}
<h1>Create / Edit Run</h1>
<form method="post">{% csrf_token %}
    {{ form.as_p }}
    <button type="submit" class="btn btn-success">Save</button>
</form>
{% endblock %}
```

**run_confirm_delete.html**
```html
{% extends 'runs/base.html' %}

{% block content %}
<h1>Delete Run</h1>
<p>Are you sure you want to delete "{{ run }}"?</p>
<form method="post">{% csrf_token %}<button type="submit" class="btn btn-danger">Yes</button></form>
{% endblock %}
```

**run_report.html**
```html
{% extends 'runs/base.html' %}

{% block content %}
<h1>Report</h1>
<div class="card">
  <div class="card-header">
    <h3>Details</h3>
  </div>
  <div class="card-body">
    <p><strong>Test Name:</strong> {{ run.testName }}</p>
    <p><strong>Start Time:</strong> {{ run.timeStart }}</p>
    <p><strong>End Time:</strong> {{ run.timeEnd }}</p>
    <p><strong>Duration:</strong> {{ run.duration }} seconds</p>
  </div>
</div>
{% endblock %}
```

## Запуск

```bash
python manage.py runserver
```

Теперь доступны:
- UI: http://127.0.0.1:8000/
- API: POST http://127.0.0.1:8000/api/run/

Пример запроса для создания прогона
```bash
curl -X POST http://127.0.0.1:8000/api/run/ \
-H "Content-Type: application/json" \
-d '{
  "testName": "SmokeTest",
  "timeStart": "2025-04-19T12:00:00Z",
  "timeEnd": "2025-04-19T12:15:00Z",
  "duration": 900.0
}'
```
