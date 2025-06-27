//
//  ChatBoxView.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//  Chat Box for text for both AI and User

import SwiftUI

struct ChatBoxView: View {
    let chats: [Chat]

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView {
                VStack(spacing: 12) {
                    ForEach(chats, id: \.id) { chat in
                        // User message aligned right
                        HStack {
                            Spacer()
                            Text(chat.user)
                                .padding(12)
                                .background(Color.blue.opacity(0.15))
                                .foregroundColor(.black)
                                .cornerRadius(12)
                                .shadow(color: .black.opacity(0.05), radius: 2, x: 0, y: 1)
                                .frame(maxWidth: 385, alignment: .trailing)
                        }

                        // AI message aligned left
                        HStack {
                            Text(chat.ai)
                                .padding(12)
                                .background(Color.gray.opacity(0.15))
                                .foregroundColor(.black)
                                .cornerRadius(12)
                                .shadow(color: .black.opacity(0.05), radius: 2, x: 0, y: 1)
                                .frame(maxWidth: 385, alignment: .leading)
                            Spacer()
                        }
                    }
                }
                .padding()
            }
            .onChange(of: chats) {
                if let lastID = chats.last?.id {
                    withAnimation {
                        proxy.scrollTo(lastID, anchor: .bottom)
                    }
                }
            }
        }
        .frame(maxHeight: 400) // limit height so it fits nicely
    }
}

#Preview {
    ChatBoxView(chats: [
        Chat(user: "Hello from User", ai: "Hello from AI"),
        Chat(user: "Another user message", ai: "AI response here"),
    ])
}
