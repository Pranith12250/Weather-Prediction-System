import firebase_admin
from firebase_admin import credentials, firestore, delete_app, get_app
from google.colab import drive
import pandas as pd
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sn
import warnings
warnings.filterwarnings(action='ignore')
import firebase_admin
from firebase_admin import credentials, firestore, delete_app, get_app
from google.colab import drive
from sklearn import preprocessing
from sklearn.model_selection import train_test_split
from sklearn import svm
from sklearn import metrics
from sklearn.metrics import mean_squared_error
from sklearn import metrics
from sklearn import tree
from sklearn import metrics
from sklearn.naive_bayes import GaussianNB
from sklearn.naive_bayes import BernoulliNB
from datetime import datetime
import pytz
# delete_app(get_app())
drive.mount('/content/drive/')

cred = credentials.Certificate('/content/drive/My Drive/ecs-ml-firebase-adminsdk-d7hjt-a0aea7bd6e.json')
df1 = pd.read_csv('/content/drive/My Drive/weather1.csv',sep=",")

df1 = df1.dropna()
firebase_admin.initialize_app(cred)

timezone = pytz.timezone("Asia/Kolkata")

db = firestore.client()
sensordata_ref = db.collection('sensordata')

def on_snapshot(doc_snapshot, changes, read_time):
    for change in changes:
        if change.type.name == 'ADDED':
            # Get the data from the added document
            doc_data = change.document.to_dict()

            # Put the data in a Pandas DataFrame
            new_instance = pd.DataFrame({
                'MinTemp': [doc_data['MinTemp']],
                'MaxTemp': [doc_data['MaxTemp']],
                'Humidity9am': [doc_data['Humidity9am']],
                'Humidity3pm': [doc_data['Humidity3pm']],
                'Pressure9am': [doc_data['Pressure9am']],
                'Pressure3pm': [doc_data['Pressure3pm']],
                'Temp9am': [doc_data['Temp9am']],
                'Temp3pm': [doc_data['Temp3pm']],
                'RainToday': [doc_data['RainToday']]
            })

            # Print the DataFrame
            putBase(new_instance)


doc_watch = sensordata_ref.on_snapshot(on_snapshot)
delete_app(get_app())



def putBase(new_instance):
    # Label Encoding
    Y = df1.RainTomorrow
    label_encoder = preprocessing.LabelEncoder()
    Y= label_encoder.fit_transform(Y)
    label_encoder = preprocessing.LabelEncoder()
    df1['RainTomorrow']= label_encoder.fit_transform(df1['RainTomorrow'])
    label_encoder = preprocessing.LabelEncoder()
    df1['RainToday']= label_encoder.fit_transform(df1['RainToday'])
    X = df1.drop(['RainTomorrow'],axis='columns')
    
    # Splitting the data in train and test
    X_train,X_test,Y_train,Y_test = train_test_split(X,Y,test_size=0.2,random_state=10)

    # Using SVM classifier with linear kernel
    clf = svm.SVC(kernel='linear') # Linear Kernel
    clf.fit(X_train, Y_train)
    y_pred = clf.predict(X_test)

    cred = credentials.Certificate('/content/drive/My Drive/ecs-ml-firebase-adminsdk-d7hjt-a0aea7bd6e.json')
    firebase_admin.initialize_app(cred)

    db = firestore.client()
    predictions_ref = db.collection('predictions')

    # Label encode the RainToday column in the new_instance DataFrame
    label_encoder = preprocessing.LabelEncoder()
    new_instance['RainToday']= label_encoder.fit_transform(new_instance['RainToday'])
    new_instance['RainToday'].unique()
    new_instance.head()

    prediction = []

    # Using SVM classifier with linear kernel
    prediction.append(int(clf.predict(new_instance)))

    # Using SVM classifier with Polynomial kernel
    model = svm.SVC(kernel='poly')
    model.fit(X_train, Y_train)
    y_pred = model.predict(X_test)

    prediction.append(int(model.predict(new_instance)))

    # Decision Tree
    model1 = tree.DecisionTreeClassifier()
    model1.fit(X_train, Y_train)
    y_pred = model1.predict(X_test)
    prediction.append(int(model1.predict(new_instance)))

    # Gaussian Naive Bayes
    model2 = GaussianNB()
    model2.fit(X_train, Y_train)
    y_pred = model2.predict(X_test)
    prediction.append(int(model2.predict(new_instance)))

    # Bernoulli Naive Bayes
    model3 = BernoulliNB()
    model3.fit(X_train, Y_train)
    y_pred = model3.predict(X_test)
    prediction.append(int(model3.predict(new_instance)))

    new_prediction = {
        'SVM Linear': prediction[0],
        'SVM Polynomial': prediction[1],
        'Decision Tree': prediction[2],
        'Gaussian Naive Bayes': prediction[3],
        'Bernoulli Naive Bayes': prediction[4]
    }

    document_id = datetime.now(tz=timezone).time().strftime('%H:%M:%S')
    doc_ref = predictions_ref.document(document_id)
    doc_ref.set(new_prediction)
    delete_app(get_app())


    
# Keep the listener running

while True:
    pass