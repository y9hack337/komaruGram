import os
import re

def rename_project(project_path, old_names, new_name):
  for dirpath, dirnames, filenames in os.walk(project_path):
    for filename in filenames:
      old_file_path = os.path.join(dirpath, filename)
      new_file_path = old_file_path
      if filename.endswith(('.java', '.xml', '.kt', '.gradle', '.py', '.md')):
        with open(new_file_path, 'r', encoding='utf-8') as f:
          file_content = f.read()
        for old_name in old_names:
          new_file_content = re.sub(old_name, new_name, file_content)
          if new_file_content != file_content:
            with open(new_file_path, 'w', encoding='utf-8') as f:
              f.write(new_file_content)
            with open(new_file_path, 'r', encoding='utf-8') as f:
              file_content = f.read()
    for dirname in dirnames:
        for old_name in old_names:
            if old_name in dirname:
                old_dir_path = os.path.join(dirpath, dirname)
                new_dir_path = os.path.join(dirpath, dirname.replace(old_name, new_name))
                os.rename(old_dir_path, new_dir_path)
                break
    for filename in filenames:
        for old_name in old_names:
            if old_name in filename:
                old_file_path = os.path.join(dirpath, filename)
                new_file_path = os.path.join(dirpath, filename.replace(old_name, new_name))
                os.rename(old_file_path, new_file_path)
                break

project_path = r'D:\cho\komaruGram'
old_names = ['cherrygram', 'Сherrygram', 'cherryGram', 'CherryGram', 'cherrygram'.upper()]
new_name = 'komarugram'
rename_project(project_path, old_names, new_name)
