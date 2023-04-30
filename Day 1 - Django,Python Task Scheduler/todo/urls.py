from django.urls import path
from . import views

app_name = 'todo'

urlpatterns = [
    path('', views.list_todos, name='todo_list'),
    path('add/', views.add_todo, name='add'),
    path('edit/<int:todo_id>/', views.edit_todo, name='todo_edit'),
    path('delete/<int:todo_id>/', views.delete_todo, name='delete'),
    path('toggle_complete/<int:todo_id>/', views.toggle_complete, name='toggle_complete'),

]
