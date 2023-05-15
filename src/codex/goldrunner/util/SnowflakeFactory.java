/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.goldrunner.util;

/**
 *
 * @author gary
 */
public class SnowflakeFactory {
	
	private long id = 0;
	
	public SnowflakeFactory() {}
	public SnowflakeFactory(long id) {
		this.id = id;
	}
	
	public long getNextId() {
		return id++;
	}
	
}
