// IBinderAnr.aidl
package com.demo.DiagnosisKit;

interface IBinderAnr {
    long syncInvoke(String name,long time);
    oneway void asyncInvoke(String name,long time);
}
