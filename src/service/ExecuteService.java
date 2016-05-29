package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ExecuteService extends Remote {
	
	/**
	 * @param code bf源代码
	 * @return 运行结果
	 * @throws RemoteException
	 */
	public String execute(String code, String param) throws RemoteException;
}
