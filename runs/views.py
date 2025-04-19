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