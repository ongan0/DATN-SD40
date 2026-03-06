import React, { useState, useEffect, useRef } from "react";
import { Button, Card, Input, Select, Typography, Avatar, Tooltip } from "antd";
import {
  MessageOutlined,
  CloseOutlined,
  SendOutlined,
  RobotOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import type { RootState } from "../../redux/store"; // Đường dẫn tuỳ project của bạn
import dayjs from "dayjs";
import { chatActions } from "../../redux/chat/chatSlice";

const { Text } = Typography;

const ChatWidget: React.FC = () => {
  const dispatch = useDispatch();

  // Lấy state từ Redux
  const { messages, isConnected, chatTarget } = useSelector(
    (state: RootState) => state.chat,
  );

  // Local state cho giao diện
  const [isOpen, setIsOpen] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // TODO: Thay "USER_123" bằng ID thực tế của User đang đăng nhập (lấy từ auth state)
  const myRoomId = "USER_123";

  // Mở kết nối WebSocket khi click mở cửa sổ chat
  useEffect(() => {
    if (isOpen && !isConnected) {
      dispatch(chatActions.connectChat(myRoomId));
    }
  }, [isOpen, isConnected, dispatch, myRoomId]);

  // Tự động cuộn xuống tin nhắn mới nhất
  useEffect(() => {
    if (isOpen) {
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages, isOpen]);

  const handleSend = () => {
    if (!inputValue.trim()) return;

    dispatch(
      chatActions.sendMessage({
        roomId: myRoomId,
        content: inputValue.trim(),
        target: chatTarget,
      }),
    );
    setInputValue("");
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSend();
    }
  };

  return (
    <div style={{ position: "fixed", bottom: 24, right: 24, zIndex: 9999 }}>
      {/* Khung Chat Window */}
      {isOpen && (
        <Card
          title={
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
            >
              <div
                style={{ display: "flex", alignItems: "center", gap: "8px" }}
              >
                <div
                  style={{
                    width: 10,
                    height: 10,
                    borderRadius: "50%",
                    backgroundColor: isConnected ? "#52c41a" : "#f5222d",
                  }}
                />
                <Text strong>Hỗ trợ trực tuyến</Text>
              </div>
              <Button
                type="text"
                icon={<CloseOutlined />}
                onClick={() => setIsOpen(false)}
                style={{ padding: 0 }}
              />
            </div>
          }
          styles={{ body: { padding: 0 } }}
          style={{
            width: 350,
            boxShadow: "0 8px 24px rgba(0,0,0,0.15)",
            marginBottom: 16,
            borderRadius: 12,
            overflow: "hidden",
          }}
        >
          {/* Phần cấu hình người nhận */}
          <div
            style={{
              padding: "12px 16px",
              backgroundColor: "#f5f5f5",
              borderBottom: "1px solid #f0f0f0",
            }}
          >
            <Text
              type="secondary"
              style={{ fontSize: 12, display: "block", marginBottom: 4 }}
            >
              Bạn muốn chat với ai?
            </Text>
            <Select
              value={chatTarget}
              onChange={(val) => dispatch(chatActions.setChatTarget(val))}
              style={{ width: "100%" }}
              options={[
                {
                  value: "AI",
                  label: (
                    <span>
                      <RobotOutlined /> Chuyên gia AI (Tư vấn Canon)
                    </span>
                  ),
                },
                {
                  value: "STAFF",
                  label: (
                    <span>
                      <UserOutlined /> Nhân viên CSKH
                    </span>
                  ),
                },
              ]}
            />
          </div>

          {/* Khu vực hiển thị tin nhắn */}
          <div
            style={{
              height: 350,
              overflowY: "auto",
              padding: "16px",
              display: "flex",
              flexDirection: "column",
              gap: "12px",
            }}
          >
            {messages.length === 0 ? (
              <div
                style={{
                  textAlign: "center",
                  marginTop: "auto",
                  marginBottom: "auto",
                  color: "#bfbfbf",
                }}
              >
                <MessageOutlined style={{ fontSize: 32, marginBottom: 8 }} />
                <p>Hãy đặt câu hỏi, chúng tôi luôn sẵn sàng hỗ trợ!</p>
              </div>
            ) : (
              messages.map((msg, idx) => {
                const isMe = msg.senderType === "CUSTOMER";
                return (
                  <div
                    key={idx}
                    style={{
                      display: "flex",
                      flexDirection: isMe ? "row-reverse" : "row",
                      gap: "8px",
                    }}
                  >
                    <Avatar
                      icon={
                        isMe ? (
                          <UserOutlined />
                        ) : msg.senderType === "AI" ? (
                          <RobotOutlined />
                        ) : (
                          <UserOutlined />
                        )
                      }
                      style={{
                        backgroundColor: isMe
                          ? "#1890ff"
                          : msg.senderType === "AI"
                            ? "#52c41a"
                            : "#fa8c16",
                      }}
                    />
                    <div style={{ maxWidth: "70%" }}>
                      <div
                        style={{
                          padding: "8px 12px",
                          borderRadius: isMe
                            ? "12px 0 12px 12px"
                            : "0 12px 12px 12px",
                          backgroundColor: isMe ? "#1890ff" : "#f0f2f5",
                          color: isMe ? "#fff" : "#000",
                          wordWrap: "break-word",
                        }}
                      >
                        {msg.content}
                      </div>
                      {msg.timestamp && (
                        <div
                          style={{
                            fontSize: "10px",
                            color: "#8c8c8c",
                            marginTop: "4px",
                            textAlign: isMe ? "right" : "left",
                          }}
                        >
                          {dayjs(msg.timestamp).format("HH:mm")}
                        </div>
                      )}
                    </div>
                  </div>
                );
              })
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Phần nhập tin nhắn */}
          <div
            style={{
              padding: "12px",
              borderTop: "1px solid #f0f0f0",
              display: "flex",
              gap: "8px",
            }}
          >
            <Input
              placeholder="Nhập nội dung..."
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyDown={handleKeyPress}
              disabled={!isConnected}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handleSend}
              disabled={!isConnected || !inputValue.trim()}
            />
          </div>
        </Card>
      )}

      {/* Nút bấm nổi (Floating Button) */}
      {!isOpen && (
        <Tooltip title="Cần hỗ trợ? Chat ngay!">
          <Button
            type="primary"
            shape="circle"
            size="large"
            icon={<MessageOutlined style={{ fontSize: 24 }} />}
            onClick={() => setIsOpen(true)}
            style={{
              width: 60,
              height: 60,
              boxShadow: "0 4px 12px rgba(24, 144, 255, 0.4)",
            }}
          />
        </Tooltip>
      )}
    </div>
  );
};

export default ChatWidget;
