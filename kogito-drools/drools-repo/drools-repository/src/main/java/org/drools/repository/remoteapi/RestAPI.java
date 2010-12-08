/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.repository.remoteapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.remoteapi.Response.Binary;
import org.drools.repository.remoteapi.Response.Text;

/**
 * This provides a simple REST style remote friendly API.
 *
 * @author Michael Neale
 *
 */
public class RestAPI {

	private final RulesRepository repo;

	private static Properties TEXT_ASSET_TYPES = loadAssetTypes();

	public RestAPI(RulesRepository repo) {
		this.repo = repo;
	}

	private static Properties loadAssetTypes() {
		Properties p = new Properties();
		try {
			p.load(RestAPI.class.getResourceAsStream("text_assets.properties"));
		} catch (IOException e) {
			System.err.println("Unable to load asset text types properties.");
			return null;
		}
		return p;
	}

	/**
	 * This works off:
	 * packages/packageName --> returns list of : asset name=ISO date time last modified,versionNumber
	 * packages/packageName/.package --> the contents of the package header
	 * packages/packageName/assetName --> the contents of the package
	 */
	public Response get(String path) throws UnsupportedEncodingException {
		String[] bits = split(path);
		if (bits.length == 1) return new Response.Text("");
		if (bits[0].equals("packages")) {
			String pkgName = bits[1];
			if (bits.length == 2) {
				return listPackage(pkgName);
			} else {
				String resourceFile = bits[2];
				return loadContent(pkgName, resourceFile);
			}
		} else {
			return new Response.Text("");//throw new IllegalArgumentException("Unable to deal with " + path);
		}

	}

	String[] split(String path) throws UnsupportedEncodingException {
		if (path.indexOf("api") > -1) {
			path = path.split("api")[1];
		}
		if (path.startsWith("/")) path = path.substring(1);
		String[] bits = path.split("/");
		for (int i = 0; i < bits.length; i++) {
			bits[i] = URLDecoder.decode(bits[i], "UTF-8");
		}
		return bits;
	}

	private Response loadContent(String pkgName, String resourceFile) throws UnsupportedEncodingException {
		PackageItem pkg = repo.loadPackage(pkgName);
		if (resourceFile.equals(".package")) {
			Text r = new Response.Text();
			r.lastModified = pkg.getLastModified();
			r.data = pkg.getStringProperty( PackageItem.HEADER_PROPERTY_NAME );
			return r;
		} else {
			if (resourceFile.indexOf("?version=") > -1) {
				String[] v = resourceFile.split("\\?version\\=");
				String version = v[1];
				String assetName = AssetItem.getAssetNameFromFileName(v[0])[0];
				AssetItem asset = pkg.loadAsset(assetName);
				if (asset.isArchived()) {
					Text r = new Text();
					r.data = "";
					return r;
				}
				if (version.equals("all")) {
					AssetHistoryIterator it =  asset.getHistory();
					StringBuilder buf = new StringBuilder();
					while(it.hasNext()) {

						AssetItem h = it.next();

						if (h.getVersionNumber() != 0) {
							String checkinComment = h.getCheckinComment();
							//String lastMo ... hmm what is needed?
							String lastMofiedBy = h.getLastContributor();
							if (lastMofiedBy == null || lastMofiedBy.equals("")) {
								lastMofiedBy = asset.getCreator();
							}
							SimpleDateFormat sdf = getISODateFormat();
							Calendar lastModDate = h.getLastModified();
							if (lastModDate == null ) {
								lastModDate = asset.getCreatedDate();
							}
							String lastModifiedOn = sdf.format(lastModDate.getTime());
							buf.append(h.getVersionNumber());
							buf.append("=");
							buf.append(lastModifiedOn + "," + lastMofiedBy + "," + checkinComment);
							if (it.hasNext()) {
								buf.append('\n');
							}
						}

					}
					Text r = new Text();
					r.lastModified = asset.getLastModified();
					r.data = buf.toString();
					return r;
				} else {
					long versionNumber = Long.parseLong(version);
					AssetHistoryIterator it =  asset.getHistory();
					while (it.hasNext()) {
						AssetItem h = it.next();
						if (h.getVersionNumber() == versionNumber) {
							return buildAssetContentResponse(pkg, h);
						}
					}
					//hmm... we didn't find it
					Text r = new Text();
					r.lastModified = asset.getLastModified();
					r.data = "Unknown version number : " + versionNumber;
					return r;
				}
			} else {
	 			String assetName = AssetItem.getAssetNameFromFileName(resourceFile)[0];
				AssetItem asset = pkg.loadAsset(assetName);
				return buildAssetContentResponse(pkg, asset);
			}

		}

	}

	private Response buildAssetContentResponse(PackageItem pkg, AssetItem asset) {
		if (asset.isBinary()) {
			Binary r = new Response.Binary();
			r.lastModified = asset.getLastModified();
			r.stream = asset.getBinaryContentAttachment();
			return r;
		} else {
			Text r = new Response.Text();
			r.lastModified = pkg.getLastModified();
			r.data = asset.getContent();
			return r;
		}
	}

	private Response listPackage(String pkgName) throws UnsupportedEncodingException {
		PackageItem pkg = repo.loadPackage(URLDecoder.decode(pkgName, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		Iterator<AssetItem> it = pkg.getAssets();
		SimpleDateFormat sdf = getISODateFormat();

		while (it.hasNext()) {
			AssetItem a = it.next();
			if (!a.isArchived()) {
				Calendar lastMod = a.getLastModified();
				if (lastMod == null) {
					lastMod = a.getCreatedDate();
				}
				sb.append(a.getName().replaceAll("\\s", "\\\\ ") + "." + a.getFormat() + "=" + sdf.format(lastMod.getTime()) + "," + a.getVersionNumber());
				sb.append('\n');
			}
		}

		Text r = new Response.Text();
		r.lastModified = pkg.getLastModified();
		r.data = sb.toString();
		return r;
	}

	private String escapeSpacesForProps(String s) {
		return s.replaceAll("\\s", "\\ ");
	}

	/**
	 * This is the format used to sent dates as text, always.
	 */
	public static SimpleDateFormat getISODateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	}

	/** post is for new content.
	 * @throws IOException
	 * @throws RulesRepositoryException */
	public void post(String path, InputStream in, String comment) throws RulesRepositoryException, IOException {
		String[] bits = split(path);
		if (bits[0].equals("packages")) {
			String fileName = bits[2];
			String[] a = fileName.split("\\.");
			if (a[1].equals("package")) {
				//new package
				PackageItem pkg = repo.createPackage(bits[1], "<added remotely>");
				pkg.updateCheckinComment(comment);
				pkg.updateStringProperty(readContent(in), PackageItem.HEADER_PROPERTY_NAME);
				repo.save();
			} else {
				//new asset
				PackageItem pkg = repo.loadPackage(bits[1]);
				AssetItem asset;
				if (pkg.containsAsset(a[0])) {
					asset = pkg.loadAsset(a[0]);
					if (asset.isArchived()) {
						asset.archiveItem(false);
					} else {
						throw new RulesRepositoryException("The file " + path + " already exists, and was not archived.");
					}
					if (asset.isBinary()) {
						asset.updateBinaryContentAttachment(in);
					} else {
						asset.updateContent(readContent(in));
					}
				} else {
					asset = pkg.addAsset(a[0], "<added remotely>");
					asset.updateFormat(a[1]);
					if (TEXT_ASSET_TYPES.containsKey(a[1])) {
						asset.updateContent(readContent(in));
					} else {
						asset.updateBinaryContentAttachment(in);
					}
				}

				asset.checkin(comment);
			}
		} else {
			throw new IllegalArgumentException("Unknown rest path for post.");
		}
	}

	private String readContent(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buf = new byte[1024];
        int len = 0;
        while ( (len = in.read( buf )) >= 0 ) {
            out.write( buf,
                       0,
                       len );
        }
        return new String(out.toByteArray());
    }

	/**
	 * Put is for updating content. It will cause a new revision to be created.
	 * need to also cope with the .package thing
	 * @throws IOException
	 */
	public void put(String path, Calendar lastModified, InputStream in, String comment) throws IOException {
		String[] bits = split(path);
		if (bits[0].equals("packages")) {
			String fileName = bits[2];
			String[] a = fileName.split("\\.");
			PackageItem pkg = repo.loadPackage(bits[1]);
			if (a[1].equals("package")) {
				//updating package header
				if (lastModified != null && pkg.getLastModified().after(lastModified)) {
					throw new RulesRepositoryException("The package was modified by: " + pkg.getLastContributor() + ", unable to write changes.");
				}
				pkg.updateStringProperty(readContent(in), PackageItem.HEADER_PROPERTY_NAME);
				pkg.checkin(comment);
				repo.save();
			} else {
				AssetItem as = pkg.loadAsset(a[0]);
				if (lastModified != null && as.getLastModified().after(lastModified)) {
					throw new RulesRepositoryException("The asset was modified by: " + as.getLastContributor() + ", unable to write changes.");
				}
				if (as.isBinary()) {
					as.updateBinaryContentAttachment(in);
				} else {
					as.updateContent(readContent(in));
				}
				as.checkin(comment);
			}

		} else {
			throw new IllegalArgumentException("Unknown rest path for put");
		}

	}

	/**
	 * Should be pretty obvious what this is for.
	 * @throws UnsupportedEncodingException
	 */
	public void delete(String path) throws UnsupportedEncodingException {
		String[] bits = split(path);
		if (bits[0].equals("packages")) {
			String fileName = bits[2].split("\\.")[0];
			AssetItem asset = repo.loadPackage(bits[1]).loadAsset(fileName);
			asset.archiveItem(true);
			asset.checkin("<removed remotely>");
		}
		else {
			throw new IllegalArgumentException("Unknown rest path for delete");
		}

	}



}
