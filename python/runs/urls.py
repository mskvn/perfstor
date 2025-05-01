from django.urls import path
from . import views

urlpatterns = [
    path('', views.run_list, name='run_list'),
    path('create/', views.run_create, name='run_create'),
    path('update/<int:pk>/', views.run_update, name='run_update'),
    path('delete/<int:pk>/', views.run_delete, name='run_delete'),
    path('api/run/', views.create_run_api, name='create_run_api'),
    path('report/<int:pk>/', views.run_report, name='run_report'),
]