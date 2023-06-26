def factorial(x):
    result = 1
    if x > 1:          
        for i in range(2,x+1):
                result = result*i
    return result
