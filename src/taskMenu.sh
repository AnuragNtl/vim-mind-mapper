#!/bin/bash

function crypt() {

        echo "Filename:"
        read file
        python crypt.py "$file" "$1"
}
echo "*************Tasks : Vim Based Command Line Mind Mapping Tool***********"
if ! which python > /dev/null
then
    echo "python not found in your path"
    exit 1
fi
if ! which groovy > /dev/null
then
    echo "groovy not found in your path"
    exit 1
fi
if ! which node > /dev/null
then
    echo "nodejs not found in your path"
    exit 1
fi
if ! [ -d graphVisualize/node_modules ]
then
    npm install --prefix graphVisualize
fi
if test $# -eq 0
then
echo "Enter tasks file : "
read tasksFile
else
    tasksFile=$1
fi

select i in read_plain_tasks_and_encrypt read_encrypted_tasks filter_encrypted_tasks visualize_encrypted_tasks edit_filters encrypted_tasks_to_csv encrypted_tasks_to_json decrypt
do

  case $i in 
    read_plain_tasks_and_encrypt )
      python access.py s "$tasksFile" r
      break
      ;;
    read_encrypted_tasks )
      python access.py p "$tasksFile" r
      break
      ;;
   filter_encrypted_tasks )
        python access.py p "$tasksFile" f
      break
      ;;
    visualize_encrypted_tasks )
      python access.py p "$tasksFile" o
      break
      ;;
    edit_filters ) 
      vim CommonFilters.groovy
      break
      ;;
    encrypted_tasks_to_csv )
      python access.py p "$tasksFile" v
      break
      ;;
    encrypted_tasks_to_json )
      python access.py p "$tasksFile" P
      break
      ;;
    decrypt )
       crypt d
        break
        ;;
  esac
done


