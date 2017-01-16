package com.jumpw.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * 
 * @Description: 序列化工具 采用ProtoStuff
 * @author jingyu
 * @date 2017-1-12 上午11:33:11
 *
 */
public class ProtoStuffSerializerUtil {

	
	/**
	 * 序列化对象
	 */
	public static <T> byte[] serializaer(T obj){
		if(obj ==null){
			throw new RuntimeException("序列化对象("+obj+")!");
		}
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
		LinkedBuffer buff = LinkedBuffer.allocate(1024*1024);
		byte[] protostuff = null;
		try {
			protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buff);
		} catch (Exception e) {
			throw new RuntimeException("序列化对象"+obj.getClass()+"发生异常！");
		}finally{
			buff.clear();
		}
		return protostuff;
	}
	/**
	 * 
	 * @Description:反序列化对象
	 * @param <T>
	 * @param paramArrayOfByte
	 * @param targerclass
	 * @return
	 */
	public static <T> T deserializaer(byte[] paramArrayOfByte,Class<T> targerclass){
		if(paramArrayOfByte==null || paramArrayOfByte.length==0){
			throw new RuntimeException("反序列化异常");
		}
		T instance = null;
		try {
			instance = targerclass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("反序列化过程中依据类型创建对象失败!", e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		Schema<T> schema = RuntimeSchema.getSchema(targerclass);
		ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
		return instance;
	}
	/**
	 * 序列化列表
	 * @param objList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] serializeList(List<T> objList){
		if(objList==null || objList.isEmpty()){
			throw new RuntimeException("序列化对象列表（"+objList+")参数异常");
		}
		Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(1024*1024);
		byte[] protostuff = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
			protostuff = bos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("序列化对象列表发生异常"+objList);
		}finally{
			buffer.clear();
			try {
				if(bos!=null){
					bos.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return protostuff;
	}
	/**
	 * 反序列化列表
	 * @param paramArrayOfByte
	 * @param targetClass
	 * @return
	 */
	public static <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
		if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
			throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
		}

		Schema<T> schema = RuntimeSchema.getSchema(targetClass);
		List<T> result = null;
		try {
			result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
		} catch (IOException e) {
			throw new RuntimeException("反序列化对象列表发生异常!", e);
		}
		return result;
	}
}
