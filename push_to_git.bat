git remote set-url origin https://github.com/fgroupindonesia/portal.git
git add .
git commit -m "Updating the Exam Questions logic with some errors under SWThreadWorker"
git push -u origin main
git gc --prune
git pull
pause