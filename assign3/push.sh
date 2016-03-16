set -e
echo "PUSHING FILES TO CSL"
rsync -av . riccardo@best-linux.cs.wisc.edu:~/private/networks/assign3
echo "PUSHING TO MININET"
ssh riccardo@best-linux.cs.wisc.edu "rsync -av ~/private/networks/assign3 mininet@mininet-06.cs.wisc.edu:~/private/networks/assign3; ant;"
echo "DONE"
