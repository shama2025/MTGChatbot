//
//  CardRulesViewController.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//

import Foundation

class CardRulesViewController: ObservableObject {
    func getCardRulesData(cardRuleID: String) async throws -> CardRules {
        let endpoint = "https://api.scryfall.com/cards/\(cardRuleID)/rulings" // Url endpoint for activity

        guard let url = URL(string: endpoint) else { // Set endpoint to be of type url
            throw RulesError.invalidURL
        }

        let (data, response) = try await URLSession.shared.data(from: url)

        guard let response: HTTPURLResponse = response as? HTTPURLResponse, response.statusCode == 200 else {
            throw RulesError.invalidResponse
        }

        do {
            let decoder = JSONDecoder() // Decode the JSON
            decoder.keyDecodingStrategy = .convertFromSnakeCase // Helps convert the data from snake to camel case

            return try decoder.decode(CardRules.self, from: data)

        } catch {
            throw RulesError.invalidData
        }
    }
}
