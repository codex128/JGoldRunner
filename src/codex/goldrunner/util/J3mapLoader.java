/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.goldrunner.util;

import codex.j3map.J3map;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;

/**
 *
 * @author gary
 */
public class J3mapLoader implements AssetLoader {

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		return new J3map(assetInfo.openStream());
	}
	
}
