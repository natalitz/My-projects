
get_ipython().magic('matplotlib inline')
import pandas as pd
import numpy as np
import math
import matplotlib.pyplot as plt
from sklearn import linear_model
import holtwinter
from statsmodels.tsa.stattools import acf, pacf
from statsmodels.tsa.arima_model import ARIMA
from statsmodels.tsa.stattools import adfuller


# Size of chunk
chunk = 5
# Size of time chunks for grouping
time_chunks = str(chunk) + 'Min'
# Number of chunks in one day
chunks_a_day = int(60/chunk * 24)

# Ask the user for input
# Each file should represent 1 day
files_paths = input ("Please enter your files' absolute paths with whitespace between:").split(" ")
num_of_files = len(files_paths)

# check for input errors
if files_paths == [''] :
    print ("Error: No files added\n")
    exit()

    
# Set training and test size according to the number of files we got as input  
train_part = int(num_of_files*0.8)
train_size = chunks_a_day*train_part
test_size = chunks_a_day*(num_of_files - train_part)
 
# Read data
path= files_paths[0]
data=pd.read_csv(path, encoding = "ISO-8859-1")
for i in range (1,num_of_files):
    path= files_paths[i]
    data=data.append(pd.read_csv(path, encoding = "ISO-8859-1"))
    data.index=pd.to_datetime(data.TimeStamp)

# Group data by chunks
a=data.groupby(pd.TimeGrouper(time_chunks)).count()

#add 1 to requests-count in case there are zero chunks
a.TimeStamp += 1



# Interesting plots
def learn_about_data():
    # Show requests per time
    a.TimeStamp.plot()
    
    # Count requests per server
    b=data.groupby([pd.TimeGrouper(time_chunks),'Host']).count()
    # Show requests per server 
    b.TimeStamp.unstack().plot(figsize=(10,10))
    
    # Count requests per continent
    b=data.groupby([pd.TimeGrouper(time_chunks),'Continent']).count()
    # Show requests per continent
    b.TimeStamp.unstack().plot(figsize=(10,10))
    
    # Count requests per response
    b=data.groupby([pd.TimeGrouper(time_chunks),'Response']).count()
    # Show requests per response
    b.TimeStamp.unstack().plot(figsize=(10,10))
    
    # Correlate host with response
    newData=data[data.Host.isin(['stamp2.app.insightsportal.visualstudio.com',
    'insightsportal-prod2-asiae.cloudapp.net','insightsportal-prod2.cloudapp.net'])]
    newData=newData[newData.Response.isin([200,204])]
    groupedHostAndResponse=newData.groupby([pd.TimeGrouper(time_chunks),'Host'
    ,'Response']).count()
    groupedHostAndResponse.TimeStamp.unstack().unstack().plot(figsize=(10,10))
    
    plt.show()


# Linear regression
# Linear regression with two attributes : last 5min and last hour
def linear_regression_with_two_attributes():
    requests = a.TimeStamp
    requests.name='calls'
    # Change d to dataframe
    requests_train=requests.to_frame() 
    # Generate the 1-shift sereis
    requests_1shift=requests.shift(1) 
    # Generate the 1-hour shift series
    requests_hour=requests.shift(12) 
    
    # Add appropriate columns
    requests_train[time_chunks]=requests_1shift
    requests_train['hour']=requests_hour
    
    # Set train data
    requests_train_x = requests_train[[time_chunks,'hour']]
    requests_train_y = requests_train['calls']
    
    # Delete all starting n/as
    requests_training_x=requests_train_x[int(chunks_a_day/2):-int(chunks_a_day/2)]
    # Delete all starting n/as
    requests_training_y=requests_train_y[int(chunks_a_day/2):-int(chunks_a_day/2)] 
    
    # Set test data
    requests_test_x = requests_train_x[-chunks_a_day:]
    requests_test_y = requests_train_y [-chunks_a_day:]
    
    # Generate the model
    regr = linear_model.LinearRegression() 
    # Run the model on training set
    regr.fit(requests_training_x, requests_training_y)
    
    #print the results
    print('Coefficients: \n', regr.coef_) 
    print('r square: %.2f' % regr.score(requests_test_x, requests_test_y))
    print("Root MSE : %.2f"
          % math.sqrt(np.mean((regr.predict(requests_test_x) - requests_test_y) ** 2)))
    
    # Predict the test set
    x=regr.predict(requests_test_x)
    y=requests_test_y
    
    # Show the prediction for test set vs the real test set
    plt.plot(x,y,'o')
    
    # Calc the trendline
    z = np.polyfit(x, y, 1)
    p = np.poly1d(z)
    # Show trend line
    plt.plot(x,p(x), "red")
    
    # The line equation:
    print ("y=%.6fx+(%.6f)"%(z[0],z[1]))
    
    plt.show()

# Linear regression with three attributes : last 5min, last hour and last 12 hours
def linear_regression_with_three_attributes():
    requests = a.TimeStamp
    requests.name='calls'
    # Change d to dataframe
    requests_train=requests.to_frame() 
    # Generate the 1-shift sereis
    requests_1shift=requests.shift(1) 
    # Generate the 1-hour shift series
    requests_hour=requests.shift(12) 
    # Generate the 12-hour shift series
    requests_thours = requests.shift(12*12)
    
    # Add appropriate columns
    requests_train[time_chunks]=requests_1shift
    requests_train['hour']=requests_hour
    requests_train['12hours'] = requests_thours
    
    # Set train data
    requests_train_x = requests_train[[time_chunks,'hour','12hours']]
    requests_train_y = requests_train['calls']
    
    # Delete all starting n/as
    requests_training_x=requests_train_x[int(chunks_a_day/2):-int(chunks_a_day/2)] 
    # Delete all starting n/as
    requests_training_y=requests_train_y[int(chunks_a_day/2):-int(chunks_a_day/2)]
    
    # Set test data
    d_test_x = requests_train_x[-chunks_a_day:]
    d_test_y = requests_train_y [-chunks_a_day:]
    
    # Generate the model
    regr = linear_model.LinearRegression() 
    regr.fit(requests_training_x, requests_training_y)
    
    # Print the results
    print('Coefficients: \n', regr.coef_) 
    print('r squared: %.2f' % regr.score(d_test_x, d_test_y))
    print("Root MSE: %.2f"
          % math.sqrt(np.mean((regr.predict(d_test_x) - d_test_y) ** 2)))
    
    # Predict the test set
    x=regr.predict(d_test_x)
    y=d_test_y
    
    # Show the prediction of test set vs the real test set
    plt.plot(x,y,'o')
    
    # Calc the trendline
    z = np.polyfit(x, y, 1)
    p = np.poly1d(z)
    
    # Show the trendline
    plt.plot(x,p(x), "red")
    
    # The line equation:
    print ("y=%.6fx+(%.6f)"%(z[0],z[1]))
    
    plt.show()


# Holt winter
# Additive
def holt_winter_additive():
    # Run the Additive version of Holtwinter
    Y, alpha, beta, gamma, rsme = holtwinter.additive(data_train_y.tolist(),
          holtWinter_seasonality, holtwinter_forecast)
    
    # Print the results
    print("\nadditive - \n alpha: " + str(alpha) + "\n beta: " + str(beta)
    + "\n gamma " + str(gamma) + "\n rmse " + str(rsme))
    
    # Show the prediction test set vs the real test set
    plt.plot(data_test_x, data_test_y,  color='black')
    plt.plot (data_test_x, Y, color = 'blue',  linewidth=2)
    plt.xticks(())
    plt.yticks(())
    
    plt.show()

# Multiplicative
def holt_winter_multiplicative():
    # Run the Multiplicative version of Holtwinter
    Y, alpha, beta, gamma, rsme = holtwinter.multiplicative(data_train_y.tolist(), holtWinter_seasonality, holtwinter_forecast)
    
    # Print the results
    print("\nmultiplicative -\n alpha: " + str(alpha) + "\n beta: " + str(beta)
    + "\n gamma " + str(gamma) + "\n rmse " + str(rsme))
    
    # Show the prediction test set vs the real test set
    plt.plot(data_test_x, data_test_y,  color='black')
    plt.plot (data_test_x, Y, color = 'red',  linewidth=1)
    plt.xticks(())
    plt.yticks(())
    
    plt.show()
    

# Third try - ARIMA
ts = a.TimeStamp

# Test stationarity
def test_stationarity(timeseries):
    
    # Determing rolling statistics
    rolmean = pd.rolling_mean(timeseries, window=12)
    rolstd = pd.rolling_std(timeseries, window=12)

    # Plot rolling statistics:
    orig = plt.plot(timeseries, color='blue',label='Original')
    mean = plt.plot(rolmean, color='red', label='Rolling Mean')
    std = plt.plot(rolstd, color='black', label = 'Rolling Std')
    plt.legend(loc='best')
    plt.title('Rolling Mean & Standard Deviation')
    plt.show(block=False)
    
    # Perform Dickey-Fuller test:
    print ('Results of Dickey-Fuller Test:')
    dftest = adfuller(timeseries, autolag='AIC')
    dfoutput = pd.Series(dftest[0:4], index=['Test Statistic','p-value','#Lags Used','Number of Observations Used'])
    for key,value in dftest[4].items():
        dfoutput['Critical Value (%s)'%key] = value
    print (dfoutput)

def arima_with_data_transformation():   
    # For Arima func, our data has to be stationary. So we check it.
    test_stationarity(ts)

    # Generate log series so we'll have stationary data
    log_d=np.log(ts) 
    
    # Show the data after the transformaion to log 
    plt.plot(log_d, color = 'red')
    plt.show()
    
    # Generate shifted sereis
    log_d_diff = log_d - log_d.shift() 
    log_d_diff.dropna(inplace=True)
    
    # Test if now we have stationariy data
    test_stationarity(log_d_diff)
    
    # Check autocorrelation
    lag_acf = acf(log_d_diff, nlags=20)
    # Check autocorrelation after reduction the previos elemnts 
    lag_pacf = pacf(log_d_diff, nlags=20, method='ols')
    
    #Plot ACF: 
    plt.subplot(121) 
    plt.plot(lag_acf)
    plt.axhline(y=0,linestyle='--',color='gray')
    plt.axhline(y=-1.96/np.sqrt(len(log_d_diff)),linestyle='--',color='gray')
    plt.axhline(y=1.96/np.sqrt(len(log_d_diff)),linestyle='--',color='gray')
    plt.title('Autocorrelation Function')
    plt.show()
    
    #Plot PACF:
    plt.subplot(122)
    plt.plot(lag_pacf)
    plt.axhline(y=0,linestyle='--',color='gray')
    plt.axhline(y=-1.96/np.sqrt(len(log_d_diff)),linestyle='--',color='gray')
    plt.axhline(y=1.96/np.sqrt(len(log_d_diff)),linestyle='--',color='gray')
    plt.title('Partial Autocorrelation Function')
    plt.tight_layout()
    plt.show()
    
    # Run Arima model
    model = ARIMA(log_d, order=(2, 1, 0))  
    results_ARIMA = model.fit(disp=-1)
    plt.plot(log_d_diff, color = 'red')
    plt.plot(results_ARIMA.fittedvalues, color = 'yellow')
    plt.show()
    
    #scale it back to the original values
    predictions_ARIMA_diff = pd.Series(results_ARIMA.fittedvalues, copy=True)
    predictions_ARIMA_diff_cumsum = -predictions_ARIMA_diff.cumsum()
    predictions_ARIMA_log = pd.Series(log_d.ix[0], index=log_d.index)
    predictions_ARIMA_log = predictions_ARIMA_log.add(predictions_ARIMA_diff_cumsum,fill_value=0)
    predictions_ARIMA = np.exp(predictions_ARIMA_log) 
    
    plt.plot(ts, color = 'yellow')
    plt.plot(predictions_ARIMA, color='green')
    plt.title('RMSE: %.4f'% np.sqrt(sum((predictions_ARIMA-ts)**2)/len(ts)))
    plt.show()
   
   
# Run funcs
learn_about_data()
# First try - linear regression
linear_regression_with_two_attributes()
linear_regression_with_three_attributes()
#second try - holt winter
holtWinter_seasonality = chunks_a_day
holtwinter_forecast = test_size
# Set training set and testing set
data_train_x = pd.to_datetime(a.index.values)[:-test_size]
data_test_x = pd.to_datetime(a.index.values)[-test_size:]
data_train_y =  a.TimeStamp.values[:-test_size]
data_test_y =  a.TimeStamp.values[-test_size:]
# Additive
holt_winter_additive()
# Multiplicative
holt_winter_multiplicative()
# Third try - ARIMA
arima_with_data_transformation()

