package com.example.pracbook;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;
public interface MyInterface {
    @LambdaFunction
    ResponseClass BookRec(RequestClass request);
}
