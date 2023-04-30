from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponseBadRequest

# Create your views here.
from django.shortcuts import render, redirect
from .models import Todo
from .forms import TodoForm
from datetime import date

def list_todos(request):
    todos = Todo.objects.all()
    return render(request, 'list_todos.html', {'todos': todos})

def add_todo(request):
    if request.method == 'POST':
        form = TodoForm(request.POST)
        if form.is_valid():
            due_date = form.cleaned_data.get('due_date')
            if due_date and due_date < date.today():
                return HttpResponseBadRequest("Due date must be in the future")
            form.save()
            return redirect('todo:todo_list')
    else:
        form = TodoForm()
    return render(request, 'add_todo.html', {'form': form})

def edit_todo(request, todo_id):
    todo = Todo.objects.get(pk=todo_id)
    if request.method == 'POST':
        form = TodoForm(request.POST, instance=todo)
        if form.is_valid():
            due_date = form.cleaned_data.get('due_date')
            if due_date and due_date < date.today():
                return HttpResponseBadRequest("Due date must be in the future")
            form.save()
            return redirect('todo:todo_list')
    else:
        form = TodoForm(instance=todo)
    return render(request, 'edit_todo.html', {'form': form, 'todo': todo})

def delete_todo(request, todo_id):
    todo = Todo.objects.get(pk=todo_id)
    todo.delete()
    return redirect('todo:todo_list')
    
def toggle_complete(request, todo_id):
    todo = get_object_or_404(Todo, pk=todo_id)
    todo.completed = not todo.completed
    todo.save()
    return redirect('todo:todo_list')
