import access
import sys
from getpass import getpass

if len(sys.argv) >= 2:
    file = sys.argv[-2]
    option = sys.argv[-1]
    password = bytes(getpass(), "utf-8")
    if option == "e":
        text = access.encrypt(password, access.getData(file));
        access.saveData(file, text);
    elif option == "d":
        text = access.decrypt(password, access.getData(file));
        access.saveData(file, text);


