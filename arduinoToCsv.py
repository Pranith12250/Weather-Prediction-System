import firebase_admin
from firebase_admin import credentials, firestore, delete_app, get_app
import serial
import csv
from datetime import datetime

# cred = credentials.Certificate('/Users/pranithsaravanan/Downloads/ecs-ml-firebase-adminsdk-d7hjt-a0aea7bd6e.json')
# firebase_admin.initialize_app(cred)

# db = firestore.client()
# predictions_ref = db.collection('sensordata')

# Open a connection to the Arduino
ser = serial.serial_for_url('/dev/cu.usbmodem11101', baudrate=9600, timeout=1)

while True:
    cred = credentials.Certificate('/Users/pranithsaravanan/Downloads/ecs-ml-firebase-adminsdk-d7hjt-a0aea7bd6e.json')
    firebase_admin.initialize_app(cred)

    db = firestore.client()
    predictions_ref = db.collection('sensordata')
    data = ser.readline().decode('utf-8').strip()
    if len(data) > 0:
        data_list = data.split(',')
        new_prediction = {
            'MinTemp': float(data_list[0]),
            'MaxTemp': float(data_list[1]),
            'Humidity9am': float(data_list[2]),
            'Humidity3pm': float(data_list[3]),
            'Pressure9am': float(data_list[4]),
            'Pressure3pm': float(data_list[5]),
            'Temp9am': float(data_list[6]),
            'Temp3pm': float(data_list[7]),
            'RainToday': str(data_list[8])
        }
        document_id = datetime.now().time().strftime('%H:%M:%S')
        doc_ref = predictions_ref.document(document_id)
        doc_ref.set(new_prediction)
    delete_app(get_app())

ser.close()