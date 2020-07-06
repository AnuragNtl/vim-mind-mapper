#! /usr/bin/env python
# -*- coding: utf-8 -*-
# vim:fenc=utf-8
#
# Copyright © 2020  <@localhost>
#
# Distributed under terms of the MIT license.

import base64
from Crypto.Cipher import AES
from Crypto.Hash import SHA256
from Crypto import Random
from random import randint
import sys
from os import system
from os import remove
from getpass import getpass
from os import path

TASK_PROCESSOR_COMMAND = "groovy OrganiserDslProcessor.groovy ";
VIEW_TASKS_COMMAND = "vim -u .vimrc "
VISUALIZE_COMMAND = "npm start --prefix graphVisualize"
GRAPH_FILE = "graphVisualize/graph.json"

def getData(filePath):
    with open(filePath, "rb") as file:
        return file.read()


def saveData(filePath, data):
    with open(filePath, "wb") as file:
        file.write(data)


def encrypt(key, text):
    key = SHA256.new(key).digest();
    iv = bytes([randint(0, 128) for x in range(0, 16)])
    aes = AES.new(key, AES.MODE_CBC, iv)
    padding = AES.block_size - len(text) % AES.block_size
    text += bytes([padding]) * padding
    data = iv + aes.encrypt(text)
    return base64.b64encode(data)

def decrypt(key, text):
    text = base64.b64decode(text)
    key = SHA256.new(key).digest()
    iv = text[:AES.block_size]
    aes = AES.new(key, AES.MODE_CBC, iv)
    data = aes.decrypt(text[AES.block_size:])
    padding = data[-1]
    if data[-padding:] != bytes([padding]) * padding:
        raise ValueError("Incorrect Password")
    return data[:-padding]


if __name__ == '__main__' and len(sys.argv) > 3:
    file = sys.argv[-2]
    password = getpass()
    if sys.argv[-3] != 's':
        data = decrypt(bytes(password, "utf-8"), getData(file))
    else:
        data = getData(file)
    tempFile = file + ".tmp"
    saveData(tempFile, data)
    if sys.argv[-1] == "f":
        system(TASK_PROCESSOR_COMMAND + tempFile + " f")
    elif sys.argv[-1] == "r":
        system(VIEW_TASKS_COMMAND + tempFile)
        system(TASK_PROCESSOR_COMMAND + tempFile + " r")
    elif sys.argv[-1] == "o":
        system(TASK_PROCESSOR_COMMAND + tempFile + " o")
    elif sys.argv[-1] == "v":
        system(TASK_PROCESSOR_COMMAND + tempFile + " v")
    elif sys.argv[-1] == 'P':
        system(TASK_PROCESSOR_COMMAND + tempFile + " P")
    if path.exists(GRAPH_FILE):
        system(VISUALIZE_COMMAND)
        if path.exists(GRAPH_FILE):
            remove(GRAPH_FILE)
    data = encrypt(bytes(password, "utf-8"), getData(tempFile))
    saveData(file, data)
    remove(tempFile)
        
    

