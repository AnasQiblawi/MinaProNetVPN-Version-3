/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package spkmods.build.ultrasshservice.aidl;
/**
 * Created by arne on 15.11.16.
 */
public interface IUltraSSHServiceInternal extends android.os.IInterface
{
  /** Default implementation for IUltraSSHServiceInternal. */
  public static class Default implements spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal
  {
    /**
         * @param replaceConnection True if the VPN is connected by a new connection.
         * @return true if there was a process that has been send a stop signal
         */
    @Override public void stopVPN() throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal
  {
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal interface,
     * generating a proxy if needed.
     */
    public static spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal))) {
        return ((spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal)iin);
      }
      return new spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
      }
      switch (code)
      {
        case TRANSACTION_stopVPN:
        {
          this.stopVPN();
          reply.writeNoException();
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
           * @param replaceConnection True if the VPN is connected by a new connection.
           * @return true if there was a process that has been send a stop signal
           */
      @Override public void stopVPN() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_stopVPN, _data, _reply, 0);
          if (!_status) {
            if (getDefaultImpl() != null) {
              getDefaultImpl().stopVPN();
              return;
            }
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal sDefaultImpl;
    }
    static final int TRANSACTION_stopVPN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public static final java.lang.String DESCRIPTOR = "spkmods.build.ultrasshservice.aidl.IUltraSSHServiceInternal";
  /**
       * @param replaceConnection True if the VPN is connected by a new connection.
       * @return true if there was a process that has been send a stop signal
       */
  public void stopVPN() throws android.os.RemoteException;
}
