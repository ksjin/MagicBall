package com.example.magicball;

import java.util.Random;

public class Answer {
	private final static String[] result = new String[] { "��", "�ƴ�", "�Ͼ��",
			"��ȸ�� ���� �ʾ�", "�׷� �� ����", "�ǽɽ�����", "���߿� ������ٰ�", "����ϴ°� ���� �� ����",
			"�ְ��", "���� �� �� ����", "���� ���ٸ�?", "�ʹ� �̸��� �ʾ�?", "���� �� �غ�",
			"�غ�� �� ������", "���ο� �� �õ��غ�", "�����ؾ���", "�ǽ��� �ʿ� ����", "���� �� ����",
			"�� �ƴѵ�", "���� �ʴٿ�", "�ʰ� �ʸ� �����ϰ� �־�", "���", "�޾� �鿩", "Ȯ����",
			"������ �ؾ���", "����ϸ� ��", "�� ����", "�׸���", "�� �� �� ����", "������ ������",
			"�ٸ� �͵� �� �����غ�", "�ʰ� �ϰ� ������� ��", "�������� ��� ���°� ����", "��ȸ�� ����" }; // 34
	private static Random random = new Random();
	public static String getAnswer(){
		int index = random.nextInt(result.length);
		return result[index];
	}
}
